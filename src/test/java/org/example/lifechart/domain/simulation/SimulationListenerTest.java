package org.example.lifechart.domain.simulation;

import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.event.GoalCreatedEvent;
import org.example.lifechart.domain.goal.event.GoalDeletedEvent;
import org.example.lifechart.domain.goal.event.GoalUpdatedEvent;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.simulation.listener.SimulationOnGoalCreatedListener;
import org.example.lifechart.domain.simulation.listener.SimulationOnGoalDeletedListener;
import org.example.lifechart.domain.simulation.listener.SimulationUpdateListener;
import org.example.lifechart.domain.simulation.repository.SimulationGoalRepository;
import org.example.lifechart.domain.simulation.service.simulation.DefaultRetirementSimulationService;
import org.example.lifechart.domain.simulation.service.simulation.SimulationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SimulationListenerTest {

    @Mock
    private SimulationService simulationService;

    @InjectMocks
    private SimulationUpdateListener simulationUpdateListener;

    @InjectMocks
    private SimulationOnGoalDeletedListener simulationOnGoalDeletedListener;

    @InjectMocks
    private SimulationOnGoalCreatedListener simulationOnGoalCreatedListener;

    @Mock
    private DefaultRetirementSimulationService defaultRetirementSimulationService;

    @Mock
    private SimulationGoalRepository simulationGoalRepository;

    @Mock
    private GoalRepository goalRepository;

    @Test
    @DisplayName("GoalUpdatedEvent 수신 시 updateSimulationsByGoalChange가 호출")
    void handleGoalUpdatedEvent_성공() {

        Long userId = 1L;
        Long goalId = 100L;
        List<Long> simulationIds = List.of(1L, 2L);

        GoalUpdatedEvent event = new GoalUpdatedEvent(userId, goalId, simulationIds);

        simulationUpdateListener.handleGoalUpdatedEvent(event);

        verify(simulationService).updateSimulationsByGoalChange(userId, goalId);
    }

    @Test
    @DisplayName("은퇴 목표 생성 이벤트가 수신되면 initializeDefaultSimulation이 호출")
    void handleRetirementGoalCreatedEvent_성공() {

        Long userId = 1L;
        Long goalId = 100L;
        List<Long> simulationIds = List.of(1L, 2L);

        GoalCreatedEvent event = new GoalCreatedEvent(userId, goalId,simulationIds);

        given(goalRepository.findCategoryById(goalId)).willReturn(Optional.of(Category.RETIREMENT));

        simulationOnGoalCreatedListener.handleGoalCreatedEvent(event);

        verify(defaultRetirementSimulationService, times(1))
                .initializeDefaultSimulation(userId, goalId);
    }

    @Test
    @DisplayName("목표 삭제 이벤트가 수신되면 handleGoalDeletedEvent가 호출")
    void handleGoalDeletedEvent_성공() {

        Long userId = 1L;
        Long goalId = 100L;

        GoalDeletedEvent event = new GoalDeletedEvent(userId, goalId);

        given(simulationGoalRepository.findSimulationIdsByGoalId(goalId))
                .willReturn(List.of());

        simulationOnGoalDeletedListener.handleGoalDeletedEvent(event);

        assertDoesNotThrow(() -> simulationOnGoalDeletedListener.handleGoalDeletedEvent(event));

    }

}



