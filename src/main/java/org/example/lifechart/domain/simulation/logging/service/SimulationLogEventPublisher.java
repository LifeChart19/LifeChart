package org.example.lifechart.domain.simulation.logging.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.request.UpdateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationResults;
import org.example.lifechart.domain.simulation.event.SimulationCreatedEvent;
import org.example.lifechart.domain.simulation.logging.enums.ChangeType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

//로그 저장 트리거
//이벤트 발행을 담당. 시뮬레이션 생성시 필요한 정보를 json으로 변환하고 simulaitionCreatedEvent를 발행함. 이벤트 발행로직을 서비스 impl에 분리하여 서비스가 본연의 책임만 가지도록.
@RequiredArgsConstructor
@Service
@Slf4j
public class SimulationLogEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper;

    //생성 이벤트 발행
    public void publishCreateEvent(Long userId, Long simulationId, List<Long> goalIds, BaseCreateSimulationRequestDto dto, SimulationResults results) {
        try {
            String paramsJson = objectMapper.writeValueAsString(dto);
            String resultsJson = objectMapper.writeValueAsString(results);

            eventPublisher.publishEvent(
                    new SimulationCreatedEvent(userId, simulationId, goalIds, paramsJson, resultsJson, ChangeType.CREATED)
            );

        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PROCESSING_FAILED);
        }
    }

    //시뮬레이션 안에서 이벤트 발행
    public void publishUpdateEventBySimulationEdit(Long userId, Long simulationId, List<Long> goalIds, UpdateSimulationRequestDto dto, SimulationResults results) {
        try {
            String paramsJson = objectMapper.writeValueAsString(dto);
            String resultsJson = objectMapper.writeValueAsString(results);

            eventPublisher.publishEvent(
                    new SimulationCreatedEvent(userId, simulationId, goalIds, paramsJson, resultsJson, ChangeType.UPDATED_BY_SIMULATION_EDIT)
            );

        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PROCESSING_FAILED);
        }
    }

    //목표 업데이트시 시뮬레이션이 업데이트 되는 경우 이벤트 발행
    public void publishUpdateEventByGoalChange(Long userId, Long simulationId, Long goalId,SimulationResults results) {
        try {
            String paramsJson = "{}"; // 사용자 입력 없음 (goal 수정이므로)
            String resultsJson = objectMapper.writeValueAsString(results);

            eventPublisher.publishEvent(
                    new SimulationCreatedEvent(userId, simulationId, List.of(goalId), paramsJson, resultsJson, ChangeType.UPDATED_BY_GOAL_CHANGE)
            );

        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PROCESSING_FAILED);
        }
    }
}