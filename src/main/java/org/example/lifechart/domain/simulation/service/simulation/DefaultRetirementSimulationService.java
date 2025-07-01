//package org.example.lifechart.domain.simulation.service.simulation;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import org.example.lifechart.common.enums.ErrorCode;
//import org.example.lifechart.common.exception.CustomException;
//import org.example.lifechart.domain.goal.entity.Goal;
//import org.example.lifechart.domain.goal.repository.GoalRepository;
//import org.example.lifechart.domain.simulation.dto.request.SimulationRetirementCalculateRequest;
//import org.example.lifechart.domain.simulation.dto.response.CreateSimulationResponseDto;
//import org.example.lifechart.domain.simulation.dto.response.SimulationResults;
//import org.example.lifechart.domain.simulation.entity.Simulation;
//import org.example.lifechart.domain.simulation.entity.SimulationGoal;
//import org.example.lifechart.domain.simulation.repository.SimulationGoalJdbcRepository;
//import org.example.lifechart.domain.simulation.repository.SimulationRepository;
//import org.example.lifechart.domain.simulation.service.calculator.CalculateAll;
//import org.example.lifechart.domain.user.entity.User;
//import org.example.lifechart.domain.user.repository.UserRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
//public class DefaultRetirementSimulationService {
//
//    private final UserRepository userRepository;
//    private final GoalRepository goalRepository;
//    private final SimulationRepository simulationRepository;
//    private final SimulationGoalJdbcRepository simulationGoalJdbcRepository;
//    private final CalculateAll calculateAll;
//
//    //goalId가져옴 은퇴목표
//    //@EventListener
//    @Transactional
//    public CreateSimulationResponseDto initializeDefaultSimulation(Long userId, Long goalId) {
//
//        //계산로직 디폴트 만들기
//        User user = validUser(userId);
//        Goal goal = validGoal(goalId, user.getId());
//
//        SimulationRetirementCalculateRequest calculateRequest =
//                SimulationRetirementCalculateRequest.builder()
//                        .title("기본 은퇴 시뮬레이션")
//                        .baseDate(LocalDate.now())
//                        .initialAsset(50_000_000L)
//                        .monthlyIncome(3_000_000L)
////                        .monthlyExpense(goal.getDetail().getMonthlyExpense()) // 은퇴 목표에서 가져옴
//                        .monthlySaving(1_500_000L)
//                        .annualInterestRate(2.0)
//                        .elapsedMonths(0)
//                        .totalMonths(1)
//                        .goalIds(List.of(goal.getId()))
//                        .build();
//
//        List<Goal> goals = List.of(goal);
//
//        SimulationResults newResults = calculateAll.calculate(
//                calculateRequest.getInitialAsset(),
//                calculateRequest.getMonthlyIncome(),
//                calculateRequest.getMonthlyExpense(),
//                calculateRequest.getMonthlySaving(),
//                calculateRequest.getAnnualInterestRate(),
//                calculateRequest.getElapsedMonths(),
//                calculateRequest.getTotalMonths(),
//                calculateRequest.getBaseDate(),
//                goals
//        );
//
//        Simulation simulation = Simulation.ofDefault(
//                calculateRequest,
//                newResults,
//                user
//        );
//
//        Map<Long, Goal> goalMap = goals.stream()
//                .collect(Collectors.toMap(Goal::getId, Function.identity()));
//
//        List<SimulationGoal> simulationGoals = calculateRequest.getGoalIds().stream()
//                .map(gid -> SimulationGoal.builder()
//                        .simulation(simulation)
//                        .goal(goalMap.get(gid)) // goalMap은 위에서 만들었음
//                        .active(true)
//                        .linkedAt(LocalDateTime.now())
//                        .build())
//                .toList();
//
//        simulation.addSimulationGoalList(simulationGoals);
//
//        simulationGoalJdbcRepository.batchInsertSimulationGoals(simulationGoals);
//        simulationRepository.save(simulation);
//
//        return CreateSimulationResponseDto.from(simulation);
//    }
//
//    private User validUser(Long userId) {
//        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
//                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
//        return user;
//    }
//    private Goal validGoal(Long goalId, Long userId) {
//        Goal goal = goalRepository.findByIdAndUserId(goalId, userId).
//                orElseThrow(()-> new CustomException(ErrorCode.GOAL_NOT_FOUND));
//        return goal;
//    }
//}
