package org.example.lifechart.domain.simulation.service.simulation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.*;
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

        //1. 소프트딜리트된 유저도 simulation생성 못하도록
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //2. Goal 목록 조회하면서 user도 같이 갖고옴.
        List<Goal> goals = goalRepository.findAllWithUserByIdAndUserId(goalIds, userId);

        //3. 존재하지 않는 goalId 검증
        if (goals.size() != goalIds.size()) {
            throw new CustomException(ErrorCode.SIMULATION_GOAL_NOT_FOUND);
        }

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

        //8. simulationGoal 목표랑 연결되기 위한 필드 목록
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

        //8. entity저장
        simulation.addSimulationGoalList(simulationGoals);

        //10. 배치인서트로 insert
        simulationGoalJdbcRepository.batchInsertSimulationGoals(simulationGoals);

        return CreateSimulationResponseDto.from(simulation);
    }

    //모든 정보가 아니라 어떤 목록이 있는지 id와 title만
    @Transactional(readOnly = true)
    public List<SimulationSummaryDto> findAllSimulationsByUserId(Long userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return simulationRepository.findAllByUser(user)
                .stream()
                .map(SimulationSummaryDto::toDto)
                .collect(Collectors.toList());

    }

    //id에 해당하는 단건 조회.
    @Transactional(readOnly = true)
    public BaseSimulationResponseDto findSimulationByUserIdAndSimulationId(Long userId, Long simulationId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        return BaseSimulationResponseDto.to(simulation);
    }

    //소프트딜리트 조회
    @Transactional(readOnly = true)
    public List<DeletedSimulationResponseDto> findAllSoftDeletedSimulations(Long userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getIsDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<Simulation> deletedSimulations = simulationRepository.findAllByUserIdAndIsDeletedTrue(userId);

        return deletedSimulations.stream()
                .map(DeletedSimulationResponseDto::toDto)
                .collect(Collectors.toList());
    }


    //    수정로직
    //비즈니스 로직(재계산, 업데이트)만 수행.
    //하나의 goalId와 연결된 여러 개의 Simulation을 찾아
    //각 Simulation의 계산을 재수행하고,
    //내부 값을 updateResults()로 갱신함
    //void로 바꾸면 클라이언트는 따로 조회해야하고 후속 비동기처리 할때 좋음.
    @Transactional
    public void updateSimulationsByGoalChange(Long userId, Long goalId, Long simulationId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 시뮬레이션 골에서 ACTIVE인 것만
        //goalid에 연결된 시뮬레이션id를 갖고와야함.
        List<SimulationGoal> simulationGoals = simulationGoalRepository
                .findAllByGoalIdAndActiveTrue(goalId);

        //다른 사용자의 simulation에 연결된 goal을 통해 접근하면 안됨.
        for (SimulationGoal sg : simulationGoals) {
            Simulation simulation = sg.getSimulation();

            if (!simulation.getUser().getId().equals(user.getId())) {
                throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
            }

            // 해당 simulation에 연결된 Goal만 조회 활성화된 goal만 갖고옴.
            List<Goal> relatedGoals = simulationGoalRepository
                    .findActiveGoalsBySimulationId(simulation.getId());

            SimulationResults newResults = calculateAll.calculate(
                    simulation.getInitialAsset(),
                    simulation.getMonthlyIncome(),
                    simulation.getMonthlyExpense(),
                    simulation.getMonthlySaving(),
                    simulation.getAnnualInterestRate(),
                    simulation.getElapsedMonths(),
                    simulation.getTotalMonths(),
                    simulation.getBaseDate(),
                    relatedGoals
            );

            simulation.updateResults(newResults);
        }
    }


//    어떤 목표랑 연결되어있는지, 시뮬레이션 하나에 복수 목표를 가지고 있지 않은지 판단하는 로직 필요.

    //시뮬레이션 안에서 update
    @Transactional
    public CreateSimulationResponseDto updateSimulationSettings(Long userId, Long simulationId, List<Long> goalIds) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        //연결할 목표 조회
        List<Goal> selectedGoals = goalRepository.findAllWithUserByIdAndUserId(goalIds, userId);

        //기존 연결은 끊기 goal은 끊을 필요가 x
        simulationGoalJdbcRepository.deactivateSimulationGoals(simulationId);

        SimulationResults newResults = calculateAll.calculate(
                simulation.getInitialAsset(),
                simulation.getMonthlyIncome(),
                simulation.getMonthlyExpense(),
                simulation.getMonthlySaving(),
                simulation.getAnnualInterestRate(),
                simulation.getElapsedMonths(),
                simulation.getTotalMonths(),
                simulation.getBaseDate(),
                selectedGoals
        );

        //임시 프록시 객체를 생성 -> sismulation전체를 조회하면 또 쿼리발생. id만 이용할거라 프록시객체로 갖고옴
        List<SimulationGoal> simulationGoals = selectedGoals.stream()
                .map(goal -> SimulationGoal.builder()
                        .simulation(Simulation.withId(simulationId))
                        .goal(goal)
                        .active(true)
                        .linkedAt(LocalDateTime.now())
                        .build())
                .toList();

        //새로 연결
        simulationGoalJdbcRepository.batchInsertSimulationGoals(simulationGoals);

        simulation.updateResults(newResults);

        return CreateSimulationResponseDto.from(simulation);

    }

    //    소프트딜리트용
    @Transactional
    public DeletedSimulationResponseDto softDeleteSimulation(Long userId, Long simulationId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(user.getId())) {
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

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        if (!simulation.isDeleted()) {
            throw new CustomException(ErrorCode.SIMULATION_DELETE_FAILED);
        }

        simulationRepository.delete(simulation);
    }
}
