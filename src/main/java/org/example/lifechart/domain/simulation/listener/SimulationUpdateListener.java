package org.example.lifechart.domain.simulation.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.domain.goal.event.GoalUpdatedEvent;
import org.example.lifechart.domain.simulation.service.simulation.SimulationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationUpdateListener {
    //은퇴 목표 생성이벤트를 수신하고, 전달받은 goalId를 추출하고, service메서드를 호출한다.

    private final SimulationService simulationService;

    //골 로직에서 이벤트 발행하면 이 메서드가 자동으로 실행될 것.
    @Async
    @EventListener
    public void handleGoalUpdatedEvent(GoalUpdatedEvent event) {

        System.out.println("GoalUpdatedEvent 수신됨: " + event);
        try {
            simulationService.updateSimulationsByGoalChange(event.getUserId(), event.getGoalId());
        } catch (Exception e) {
            log.error("시뮬레이션 재계산 실패", e);
        }
    }
}