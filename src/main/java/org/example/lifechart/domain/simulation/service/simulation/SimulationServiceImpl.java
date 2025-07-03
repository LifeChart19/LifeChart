package org.example.lifechart.domain.simulation.service.simulation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.goal.repository.GoalRetirementRepository;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.request.UpdateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.*;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
import org.example.lifechart.domain.simulation.logging.service.SimulationLogEventPublisher;
import org.example.lifechart.domain.simulation.repository.SimulationGoalJdbcRepository;
import org.example.lifechart.domain.simulation.repository.SimulationGoalRepository;
import org.example.lifechart.domain.simulation.repository.SimulationRepository;
import org.example.lifechart.domain.simulation.service.calculator.CalculateAll;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.infra.client.AccountClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final SimulationLogEventPublisher eventPublisher;
    private final GoalRetirementRepository goalRetirementRepository;
    private final AccountClient accountClient;

    //사용자가 목표는 그대로 두고, 시뮬레이션만 새로운 파라미터로 돌림
    @Override
    @Transactional
    public CreateSimulationResponseDto saveSimulation(BaseCreateSimulationRequestDto dto, Long userId, List<Long> goalIds) {
//        AuthUtil.validateUserAccess(userId);
//        MockBankApiResponse<AccountResponse> accountResponse = accountClient.getAccount(userId);
//        AccountResponse account = accountResponse.getData();
//
//        if (accountResponse.getData() == null) {
//            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
//        }
//
//        long initialAsset = account.getBalance().longValue();

        //1. 소프트딜리트된 유저도 simulation생성 못하도록
        User user = validUser(userId);

        List<Goal> goals = goalRepository.findAllWithUserByIdAndUserId(goalIds, userId).stream()
                .filter(goal -> goal.getStatus() == Status.ACTIVE)
                .toList();

        if (goals.size() != goalIds.size()) {
            throw new CustomException(ErrorCode.SIMULATION_GOAL_NOT_FOUND);
        }

        Goal representativeGoal = goals.stream()
                .filter(goal -> goal.getCategory() == Category.RETIREMENT)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.GOAL_RETIREMENT_NOT_FOUND));

        GoalRetirement retirementDetail = goalRetirementRepository.findByGoalId(representativeGoal .getId())
                .orElseThrow(() -> new CustomException(ErrorCode.GOAL_RETIREMENT_NOT_FOUND));

        LocalDate expectedDeathDate = retirementDetail.getExpectedDeathDate();

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
                expectedDeathDate, //기대수명은 업데이트에서 받아야하고 save에서는 없어야 함.
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
                .map(createGoalId -> SimulationGoal.builder()
                        .simulation(simulation)
                        .goal(goalMap.get(createGoalId))
                        .active(true)
                        .linkedAt(LocalDateTime.now())
                        .build())
                .toList();

        //8. entity저장
        simulation.addSimulationGoalList(simulationGoals);

        //10. 배치인서트로 insert
        simulationGoalJdbcRepository.batchInsertSimulationGoals(simulationGoals);

        eventPublisher.publishCreateEvent(
                user.getId(),
                simulation.getId(),
                goalIds,
                dto,
                results
        );

        return CreateSimulationResponseDto.from(simulation);
    }

    //모든 정보가 아니라 어떤 목록이 있는지 id와
    @Override
    @Transactional(readOnly = true)
    public List<SimulationSummaryDto> findAllSimulationsByUserId(Long userId) {

        User user = validUser(userId);

        return simulationRepository.findAllByUser(user)
                .stream()
                .map(SimulationSummaryDto::toDto)
                .collect(Collectors.toList());

    }

    //id에 해당하는 단건 조회.
    @Override
    @Transactional(readOnly = true)
    public BaseSimulationResponseDto findSimulationByUserIdAndSimulationId(Long userId, Long simulationId) {

        User user = validUser(userId);

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        return BaseSimulationResponseDto.to(simulation);
    }

    //소프트딜리트 조회
    @Override
    @Transactional(readOnly = true)
    public List<DeletedSimulationResponseDto> findAllSoftDeletedSimulations(Long userId) {

        User user = validUser(userId);

        if (user.getIsDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<Simulation> deletedSimulations = simulationRepository.findAllByUserIdAndIsDeletedTrue(userId);

        return deletedSimulations.stream()
                .map(DeletedSimulationResponseDto::toDto)
                .collect(Collectors.toList());
    }

    //목표 수정->시뮬레이션 수정 로직
    @Override
    @Transactional
    public void updateSimulationsByGoalChange(Long userId, Long goalId) {

        User user = validUser(userId);
        Goal updateGoal = validGoal(goalId, user.getId());

        // 시뮬레이션골 연결된 ACTIVE 항목만 조회
        List<SimulationGoal> simulationGoals =
                simulationGoalRepository.findAllByGoalIdAndSimulationUserIdAndActiveTrue(updateGoal.getId(), user.getId());

        if (simulationGoals.isEmpty()) {
            throw new CustomException(ErrorCode.SIMULATION_NOT_FOUND_BY_GOAL);
        }

        for (SimulationGoal sg : simulationGoals) {
            Simulation simulation = sg.getSimulation();

            // 시뮬레이션에 연결된 모든 활성화된 Goal 가져오기
            List<Goal> relatedGoals = simulationGoalRepository
                    .findActiveGoalsBySimulationId(simulation.getId());

            //기대수명을 위한 은퇴 카테고리 Goal 필수 조회
            Goal retirementGoal = relatedGoals.stream()
                    .filter(Objects::nonNull)
                    .filter(Goal::isRetirementCategory)
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.GOAL_RETIREMENT_NOT_FOUND));

            if (!retirementGoal.getUser().getId().equals(user.getId())) {
                throw new CustomException(ErrorCode.GOAL_RETIREMENT_NOT_FOUND);
            }

            //기대수명 정보 조회
            GoalRetirement retirementDetail = goalRetirementRepository.findByGoalId(retirementGoal.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.GOAL_RETIREMENT_NOT_FOUND));

            LocalDate newExpectedDeathDate = retirementDetail.getExpectedDeathDate();

            SimulationResults newResults = calculateAll.calculate(
                    simulation.getInitialAsset(),
                    simulation.getMonthlyIncome(),
                    simulation.getMonthlyExpense(),
                    simulation.getMonthlySaving(),
                    simulation.getAnnualInterestRate(),
                    simulation.getElapsedMonths(),
                    simulation.getTotalMonths(),
                    simulation.getBaseDate(),
                    newExpectedDeathDate,
                    relatedGoals
            );

            simulation.updateResults(newResults);

            eventPublisher.publishUpdateEventByGoalChange(
                    user.getId(),
                    simulation.getId(),
                    updateGoal.getId(),
                    newResults
            );
        }
    }

    //시뮬레이션 안에서 update
    @Override
    @Transactional
    public CreateSimulationResponseDto updateSimulationSettings(Long userId, Long simulationId, List<Long> goalIds, UpdateSimulationRequestDto dto) {

        User user = validUser(userId);
        //삭제된 goalId는 가져오면 안됨.
        List<Goal> goals = goalRepository.findAllWithUserByIdAndUserId(goalIds, userId).stream()
                .filter(goal -> goal.getStatus() == Status.ACTIVE)
                .toList();

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        //여기서 은퇴가 연결이 끊기면 안됨. noneMatch는 조건을 만족하는게 없어야함.
        if (goals.stream().noneMatch(goal -> goal.getCategory() == Category.RETIREMENT)) {
            throw new CustomException(ErrorCode.RETIREMENT_GOAL_REQUIRED);
        }

        //시뮬레이션에서 기존에 [1,2,3]의 목표를 담고 있었다면 여기서 [1,2]로 변경했을 때 3은 연결이 끊기게 됨
        simulationGoalJdbcRepository.deactivateSimulationGoals(simulationId);

        //연결할 목표 조회
        //goalRepository에서 시뮬레이션
        List<Goal> selectedGoals = goalRepository.findAllWithUserByIdAndUserId(goalIds, userId);

        if (selectedGoals.size() != goalIds.size()) {
            throw new CustomException(ErrorCode.GOAL_NOT_FOUND);
        }

        List<SimulationGoal> newSimulationGoals = selectedGoals.stream()
                .map(goal -> SimulationGoal.builder()
                        .simulation(Simulation.withId(simulationId))
                        .goal(goal)
                        .active(true)
                        .linkedAt(LocalDateTime.now())
                        .build())
                .toList();

        simulationGoalJdbcRepository.batchInsertSimulationGoals(newSimulationGoals);

        simulation.updateFieldsFromDto(dto);

        //현재 디폴트 은퇴시뮬레이션이 비동기처리까지 완료된 상태가 아니라서 임의로 기대수명을 넣어두었습니다
        LocalDate expectedDeathDate = LocalDate.now().plusYears(60);

        SimulationResults newResults = calculateAll.calculate(
                simulation.getInitialAsset(),
                simulation.getMonthlyIncome(),
                simulation.getMonthlyExpense(),
                simulation.getMonthlySaving(),
                simulation.getAnnualInterestRate(),
                simulation.getElapsedMonths(),
                simulation.getTotalMonths(),
                simulation.getBaseDate(),
                expectedDeathDate,
                selectedGoals
        );
        simulation.updateResults(newResults);

        eventPublisher.publishUpdateEventBySimulationEdit(
                user.getId(),
                simulation.getId(),
                goalIds,
                dto,
                newResults
        );

        return CreateSimulationResponseDto.from(simulation);
    }

    //    소프트딜리트용
    @Override
    @Transactional
    public DeletedSimulationResponseDto softDeleteSimulation(Long userId, Long simulationId) {

        User user = validUser(userId);

        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

        if (!simulation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        //이미 소프트딜리트가 되었을 경우에도
        if (simulation.isDeleted()) {
            throw new CustomException(ErrorCode.SIMULATION_BAD_REQUEST);
        }

        //더 간편한 예외처리가 있는지 생각해보기,
        List<Goal> linkedGoals = simulationGoalRepository.findActiveGoalsBySimulationId(simulationId);

        //여기서 은퇴가 연결이 끊기면 안됨. anyMatch는 하나라도 조건이 만족하면 true
        if (linkedGoals.stream().anyMatch(goal -> goal.getCategory() == Category.RETIREMENT)) {
            throw new CustomException(ErrorCode.RETIREMENT_GOAL_SIMULATION_CANNOT_BE_DELETED);
        }

        //시뮬레이션에 연결되어있는 목표가 모두 사라진다면
        List<SimulationGoal> activeSimulationGoals = simulationGoalRepository.findBySimulationIdAndActiveTrue(simulationId);

        if (!activeSimulationGoals.isEmpty()) {
            throw new CustomException(ErrorCode.SIMULATION_LINKED_ENTITY_EXISTS);
        }

        simulation.softDelete();

        return DeletedSimulationResponseDto.toDto(simulation);

    }

    @Override
    @Transactional
    public void deleteSimulation(Long userId, Long simulationId) {

        User user = validUser(userId);

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

    private User validUser(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user;
    }

    private Goal validGoal(Long goalId, Long userId) {
        return goalRepository.findByIdAndUserIdAndStatus(goalId, userId, Status.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.GOAL_NOT_FOUND));
    }
}
