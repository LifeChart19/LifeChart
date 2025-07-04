package org.example.lifechart.domain.simulation.service.simulation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.goal.repository.GoalRetirementRepository;
import org.example.lifechart.domain.simulation.dto.request.SimulationRetirementCalculateRequest;
import org.example.lifechart.domain.simulation.dto.response.SimulationResults;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
import org.example.lifechart.domain.simulation.repository.SimulationGoalJdbcRepository;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultRetirementSimulationService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final SimulationRepository simulationRepository;
    private final SimulationGoalJdbcRepository simulationGoalJdbcRepository;
    private final CalculateAll calculateAll;
    private final GoalRetirementRepository goalRetirementRepository;
    private final AccountClient accountClient;

    //goalId가져옴 은퇴목표
    @Transactional
    public void initializeDefaultSimulation(Long userId, Long goalId) {

//        AuthUtil.validateUserAccess(userId);
//        MockBankApiResponse<AccountResponse> accountResponse = accountClient.getAccount(userId);
//        AccountResponse account = accountResponse.getData();
//
//        if (accountResponse.getData() == null) {
//            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
//        }
//
//        long initialAsset = account.getBalance().longValue();

        //계산로직 디폴트 만들기
        User user = validUser(userId);
        Goal goal = validGoal(goalId, user.getId());

        GoalRetirement retirementDetail = goalRetirementRepository.findByGoalId(goal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.GOAL_RETIREMENT_NOT_FOUND));

        SimulationRetirementCalculateRequest calculateRequest =
                SimulationRetirementCalculateRequest.builder()
                        .title("기본 은퇴 시뮬레이션")
                        .baseDate(LocalDate.now())
                        .initialAsset(3_000_000L)
                        .monthlyIncome(3_000_000L)
                        .monthlyExpense(retirementDetail.getMonthlyExpense())
                        .monthlySaving(1_500_000L)
                        .annualInterestRate(2.0)
                        .elapsedMonths(0)
                        .totalMonths(1)
                        .goalIds(List.of(goal.getId()))
                        .build();

        List<Goal> goals = List.of(goal);

        LocalDate expectedDeathDate = retirementDetail.getExpectedDeathDate();

        SimulationResults newResults = calculateAll.calculate(
                calculateRequest.getInitialAsset(),
                calculateRequest.getMonthlyIncome(),
                calculateRequest.getMonthlyExpense(),
                calculateRequest.getMonthlySaving(),
                calculateRequest.getAnnualInterestRate(),
                calculateRequest.getElapsedMonths(),
                calculateRequest.getTotalMonths(),
                calculateRequest.getBaseDate(),
                expectedDeathDate,
                goals
        );

        Simulation simulation = Simulation.ofDefault(
                calculateRequest,
                newResults,
                user
        );

        simulationRepository.save(simulation);

        Map<Long, Goal> goalMap = goals.stream()
                .collect(Collectors.toMap(Goal::getId, Function.identity()));

        List<SimulationGoal> simulationGoals = calculateRequest.getGoalIds().stream()
                .map(gid -> SimulationGoal.builder()
                        .simulation(simulation)
                        .goal(goalMap.get(gid)) // goalMap은 위에서 만들었음
                        .active(true)
                        .linkedAt(LocalDateTime.now())
                        .build())
                .toList();

        simulation.addSimulationGoalList(simulationGoals);

        simulationGoalJdbcRepository.batchInsertSimulationGoals(simulationGoals);

    }

    private User validUser(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user;
    }
    private Goal validGoal(Long goalId, Long userId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId).
                orElseThrow(()-> new CustomException(ErrorCode.GOAL_NOT_FOUND));
        return goal;
    }
}
