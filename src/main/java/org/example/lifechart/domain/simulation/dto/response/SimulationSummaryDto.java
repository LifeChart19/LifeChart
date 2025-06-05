package org.example.lifechart.domain.simulation.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.lifechart.domain.simulation.entity.Simulation;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SimulationSummaryDto {

    private Long simulationId;

    private String title;


    public static SimulationSummaryDto toDto(Simulation simulation) {
        return SimulationSummaryDto.builder()
                    .simulationId(simulation.getId())
                    .title(simulation.getTitle())
                    .build();
    }
}