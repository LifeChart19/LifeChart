package org.example.lifechart.domain.simulation.service.simulation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.CreateSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.DeletedSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationResults;
import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
import org.example.lifechart.domain.simulation.repository.SimulationGoalJdbcRepository;
import org.example.lifechart.domain.simulation.repository.SimulationGoalRepository;
import org.example.lifechart.domain.simulation.repository.SimulationRepository;
import org.example.lifechart.domain.simulation.service.calculator.CalculateAll;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final GoalRepository goalRepository;
    private final SimulationGoalJdbcRepository simulationGoalJdbcRepository;
    private final UserRepository userRepository;
    private final SimulationGoalRepository simulationGoalRepository;
    private final CalculateAll calculateAll;

    //사용자가 목표는 그대로 두고, 시뮬레이션만 새로운 파라미터로 돌림
    @Transactional
    public CreateSimulationResponseDto saveSimulation(BaseCreateSimulationRequestDto dto, Long userId, List<Long> goalIds) {

        //0. 소프트딜리트된 유저도 simulation생성 못하도록
        User user = userRepository.findById(userId)
                .filter(u -> !u.getIsDeleted())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 1. Goal 목록 조회하면서 user도 같이 갖고옴.
        List<Goal> goals = goalRepository.findAllWithUserByIdAndUserId(goalIds, userId);


        // 2. 존재하지 않는 goalId 검증
        if (goals.size() != goalIds.size()) {
            throw new CustomException(ErrorCode.SIMULATION_GOAL_NOT_FOUND);
        }

        //3. 유효성 검증 로직 웬만한건 valid로 처리 복잡성높아지고 테스트 검증로직이 너무 늘어날 것 같아서 보류.
        //simulationValidator.validateSimulationParams(dto);
        //   수식 -> monthlyIncome, monthlyExpense 음수 여부, 최소값, initialAsset null여부

        //4. 계산로직 수행 -> 더 효율적인 방법 고민 필요
        SimulationResults results = calculateAll.calculate(
                dto.getInitialAsset(),
                dto.getMonthlyIncome(),
                dto.getMonthlyExpense(),
                dto.getMonthlySaving(),
                dto.getAnnualInterestRate(),
                dto.getElapsedMonths(),
                dto.getTotalMonths(),
                dto.getBaseDate(),
                goals
        );

        //5. 빌더 패턴 시뮬레이션 생성
        //-앞으로 모아야하는 금액
        //-목표 달성까지 예상날짜 반환
        //-현재 달성률 계산
        //-매달 예상 달성률 리스트 반환
        //-매달 자산 변화
        Simulation simulation = Simulation.createSimulation(dto, results, user);


        //6. simulationGoal생성시 simulation.getId가 필요
        simulationRepository.save(simulation);


        //7. 수정: Goal리스트를 Map으로 변환하고 key는 goalid, value는 goal객체 자체.
        //map key로 goal의 id값이 들어가도록 반환.
        //goalId를 갖고오면서 N+1문제를 해결하기 위해 리펙토링 적용한 것. -> DB조회 한 번 하고 끝
        Map<Long, Goal> goalMap = goals.stream()
                .collect(Collectors.toMap(Goal::getId, Function.identity()));

        //. simulationGoal 목표랑 연결되기 위한 필드 목록
        //unlinked는 null이 됨.
        List<SimulationGoal> simulationGoals = goalIds.stream()
                .map(goalId -> {
                    return SimulationGoal.builder()
                            .simulation(simulation)
                            .goal(goalMap.get(goalId))
                            .active(true)
                            .linkedAt(LocalDateTime.now())
                            .build();
                })
                .toList();

        //7. entity저장
        simulation.addSimulationGoalList(simulationGoals);

        //8. 배치인서트로 insert
        simulationGoalJdbcRepository.batchInsertSimulationGoals(simulationGoals);

        return CreateSimulationResponseDto.from(simulation);
    }

    //모든 정보가 아니라 어떤 목록이 있는지 id와 title만
    @Transactional(readOnly = true)
    public List<SimulationSummaryDto> findAllSimulationsByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .filter(u -> !u.getIsDeleted())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return simulationRepository.findAllByUser(user)
                .stream()
                .map(SimulationSummaryDto::toDto)
                .collect(Collectors.toList());

    }


//    //id에 해당하는 단건 조회. 어떤 필드만 보여줘야할지 dto를 다시 세팅해야 할듯.
//    //패치조인으로..
//    public BaseSimulationResponseDto findSimulationById(User user , Long simulationId) {
//
//        //커스텀예외처리 필요
//        Simulation simulation = simulationRepository.findByIdWithResults(simulationId)
//                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));
//
//        if (!simulation.getUser().getId().equals(user.getId())) {
//            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
//        }
//
//        SimulationResults results = simulation.getResults();
//
//        return BaseSimulationResponseDto.dto(simulation, results);
//    }

//    @Transactional(readOnly = true)
//    public List<DeletedSimulationResponseDto> findAllSoftDeletedSimulations(Long userId) {
//
//        List<Simulation> deletedSimulations = simulationRepository.findAllByUserIdAndIsDeletedTrue(userId);
//
//        return deletedSimulations.stream()
//                .map(DeletedSimulationResponseDto::toDto)
//                .collect(Collectors.toList());
//    }

//    수정로직
//    @Transactional
//    public BaseSimulationResponseDto updateSimulation(List<SimulationGoal> newSimulationGoals,Simulation simulation) {
//        // 1. goal이 목표 수정했는지  -> 목표가 수정되면 목표 ID를 확보함. 업데이트된 골을 service메서드에서 dto값을 가져옴.
//        Goal updateGoal = goalService.updateGoal(goalUpdateDto);
//
//        //goalId필요함.(선택한 goal이 무엇인지..)
//        Long goalId = updateGoal.getId();
//
//        //2. 수정된 목표가 연결된 시뮬레이션 조회 goalId에 연결된 시뮬레이션이 여러개일 수도 있음 -> 가져올 때 중복 제거
//        List<Simulation> simulations = simulationGoalRepository.findDistinctSimulationsByGoalId(goalId);
//
//        //3. 각 시뮬레이션에 대해 재계산
//        //수정: Goal리스트를 Map으로 변환하고 key는 goalid, value는 goal객체 자체.
//        //map key로 goal의 id값이 들어가도록 반환하면
//        Map<Long, Goal> goalMap = goals.stream()
//                .collect(Collectors.toMap(Goal::getId, Function.identity()));
//
//            //5. 시뮬레이션과 시뮬레이션 골 사이의 연관관계 재설정.
//            //시뮬레이션 수정 흐름 설계 중에 기존 Simulation에 연결된 SimulationGoal들이 존재.
//            //업데이트하는 로직에서도 필드를 같이 관리 해야함.
//        //goalId에 연결된 모든 Simulation을 확인해봐야함.
//        for(Simulation updateSimulation : simulations) {
//            simulationGoalJdbcRepository.deactivateSimulationGoals(updateSimulation.getId());
//        }
//            simulationGoalJdbcRepository.batchInsertSimulationGoals(newSimulationGoals);
//
//        //시뮬레이션은 현재 목표와 바꾸고싶은 목표를 시뮬레이션 해서 보여줄 수 있다.
//
//        return BaseSimulationResponseDto.toDto(simulation);
//
//    }
//    어떤 목표랑 연결되어있는지, 시뮬레이션 하나에 복수 목표를 가지고 있지 않은지 판단하는 로직 필요.


//    소프트딜리트용
    @Transactional
    public DeletedSimulationResponseDto softDeleteSimulation(Long userId, Long simulationId) {

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        //이미 소프트딜리트가 되었을 경우에도
        if (simulation.isDeleted()) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        //시뮬레이션에 연결되어있는 목표가 모두 사라진다면
        List<SimulationGoal> activeSimulationGoals = simulationGoalRepository.findBySimulationIdAndActiveTrue(simulationId);

        if (!activeSimulationGoals.isEmpty()) {
            throw new CustomException(ErrorCode.SIMULATION_LINKED_ENTITY_EXISTS);
        }

        simulation.softDelete();

        return DeletedSimulationResponseDto.toDto(simulation);

    }


    @Transactional
    public void deleteSimulation(Long userId, Long simulationId) {

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        if (!simulation.isDeleted()) {
            throw new CustomException(ErrorCode.SIMULATION_DELETE_FAILED);
        }

        simulationRepository.delete(simulation);
    }
}
