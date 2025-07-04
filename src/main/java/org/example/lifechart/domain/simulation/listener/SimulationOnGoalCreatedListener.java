package org.example.lifechart.domain.simulation.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.event.GoalCreatedEvent;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.simulation.service.simulation.DefaultRetirementSimulationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationOnGoalCreatedListener {

    private final GoalRepository goalRepository;
    private final DefaultRetirementSimulationService retirementSimulationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGoalCreatedEvent(GoalCreatedEvent event) {
        Long userId = event.getUserId();
        Long goalId = event.getGoalId();

        // 목표 카테고리 확인 (은퇴 목표만 처리)
        Category category = goalRepository.findCategoryById(goalId)
                .orElse(null);

        if (category == Category.RETIREMENT) {
            try {
                log.info("[SimulationOnGoalCreatedListener] 은퇴 목표 감지 → 시뮬레이션 생성 ");
                retirementSimulationService.initializeDefaultSimulation(userId, goalId);
            } catch (Exception e) {
                log.error("[SimulationOnGoalCreatedListener] 은퇴 시뮬레이션 생성 실패", e);
            }
        }
    }
}