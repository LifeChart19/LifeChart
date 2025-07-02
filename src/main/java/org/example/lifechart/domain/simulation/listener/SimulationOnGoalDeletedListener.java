package org.example.lifechart.domain.simulation.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.event.GoalDeletedEvent;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.simulation.dto.response.SimulationResults;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.repository.SimulationGoalRepository;
import org.example.lifechart.domain.simulation.repository.SimulationRepository;
import org.example.lifechart.domain.simulation.service.calculator.CalculateAll;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationOnGoalDeletedListener {

    private final SimulationRepository simulationRepository;
    private final SimulationGoalRepository simulationGoalRepository;
    private final CalculateAll calculateAll;
    private final GoalRepository goalRepository;

    @Async
    @TransactionalEventListener
    public void handleGoalDeletedEvent(GoalDeletedEvent event) {

        Long userId = event.getUserId();
        Long goalId = event.getGoalId();

        List<Long> simulationIds = simulationGoalRepository.findSimulationIdsByGoalId(goalId);

        // 2. 연결 데이터 삭제
        simulationGoalRepository.deleteByGoalId(goalId);

        // 3. 시뮬레이션별 처리
        for (Long simulationId : simulationIds) {

            List<Long> remainingGoalIds = simulationGoalRepository.findGoalIdsBySimulationId(simulationId);

            if (remainingGoalIds.isEmpty()) {
                simulationRepository.softDeleteById(simulationId);
                continue;
            }

            List<Goal> selectedGoals = goalRepository.findAllById(remainingGoalIds);


            Simulation simulation = simulationRepository.findById(simulationId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SIMULATION_NOT_FOUND));

            LocalDate expectedDeathDate = LocalDate.now().plusYears(60);

            SimulationResults results = calculateAll.calculate(
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

            simulation.updateResults(results);
        }
    }
}