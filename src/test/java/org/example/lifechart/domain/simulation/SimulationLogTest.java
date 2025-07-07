package org.example.lifechart.domain.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.lifechart.domain.simulation.logging.dto.SimulationLogSaveDto;
import org.example.lifechart.domain.simulation.logging.entity.SimulationLog;
import org.example.lifechart.domain.simulation.logging.enums.ChangeType;
import org.example.lifechart.domain.simulation.logging.repository.SimulationLogRepository;
import org.example.lifechart.domain.simulation.logging.service.SimulationLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class SimulationLogTest {

    @Mock
    private SimulationLogRepository simulationLogRepository;

    @InjectMocks
    private SimulationLogServiceImpl simulationLogService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        simulationLogRepository = mock(SimulationLogRepository.class);
        objectMapper = mock(ObjectMapper.class);
        simulationLogService = new SimulationLogServiceImpl(simulationLogRepository, objectMapper);
    }

    @Test
    @DisplayName("시뮬레이션 로그 저장에 성공")
    void saveLog_success() throws Exception {

        List<Long> goalIds = List.of(1L, 2L);
        String goalIdsJson = "[1,2]";

        SimulationLogSaveDto dto = SimulationLogSaveDto.builder()
                .userId(100L)
                .simulationId(200L)
                .goalIds(goalIds)
                .params("{\"income\":10000}")
                .results("{\"result\":50000}")
                .changeType(ChangeType.CREATED)
                .build();

        when(objectMapper.writeValueAsString(goalIds)).thenReturn("[1,2]");

        simulationLogService.saveLog(dto);

        ArgumentCaptor<SimulationLog> captor = ArgumentCaptor.forClass(SimulationLog.class);
        verify(simulationLogRepository, times(1)).save(captor.capture());

        SimulationLog savedLog = captor.getValue();

        assertThat(savedLog.getUserId()).isEqualTo(100L);
        assertThat(savedLog.getSimulationId()).isEqualTo(200L);
        assertThat(savedLog.getGoalIds()).isEqualTo(goalIdsJson);
        assertThat(savedLog.getParams()).isEqualTo("{\"income\":10000}");
        assertThat(savedLog.getResults()).isEqualTo("{\"result\":50000}");
        assertThat(savedLog.getChangeType()).isEqualTo(ChangeType.CREATED);
    }
}
