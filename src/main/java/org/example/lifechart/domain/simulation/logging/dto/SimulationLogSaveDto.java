package org.example.lifechart.domain.simulation.logging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private ChangeType changeType;
}