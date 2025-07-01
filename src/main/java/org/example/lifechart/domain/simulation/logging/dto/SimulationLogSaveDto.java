package org.example.lifechart.domain.simulation.logging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.lifechart.domain.simulation.logging.enums.ChangeType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulationLogSaveDto {

    private Long userId;

    private Long simulationId;

    private List<Long> goalIds;

    private String params;

    private String results;

    private LocalDateTime createdAt;

    private ChangeType changeType;
}