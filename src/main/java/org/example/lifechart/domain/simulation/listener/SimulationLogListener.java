package org.example.lifechart.domain.simulation.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.domain.simulation.event.SimulationCreatedEvent;
import org.example.lifechart.domain.simulation.logging.dto.SimulationLogSaveDto;
import org.example.lifechart.domain.simulation.logging.service.SimulationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

//발행된 시뮬레이션 로그 이벤트를 저장하기 위함. builder로 해서 저장. 근데
//이벤트를 발행했고, 그 이벤트를 발행을 받아주는 클래스가 필요해서
//얘가 그 역할을 하는 것임.
//async는 시뮬레이션 부가작업 -> 성능과 응답속도 최적화를 위한 비동기 처리.
@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationLogListener {

    private final SimulationLogService simulationLogService;


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSimulationCreated(SimulationCreatedEvent event) {
        try {
            SimulationLogSaveDto dto = SimulationLogSaveDto.builder()
                    .userId(event.getUserId())
                    .simulationId(event.getSimulationId())
                    .goalIds(event.getGoalIds())
                    .params(event.getParams())
                    .results(event.getResults())
                    .createdAt(LocalDateTime.now())
                    .changeType(event.getChangeType())
                    .build();

            simulationLogService.saveLog(dto);

        } catch (Exception e) {
            log.error("시뮬레이션 로그 저장이 실패했습니다.", e);
        }
    }
}
