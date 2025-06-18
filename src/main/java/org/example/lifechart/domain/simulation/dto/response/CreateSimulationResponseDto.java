package org.example.lifechart.domain.simulation.dto.response;

import lombok.*;
import org.example.lifechart.domain.simulation.entity.Simulation;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
//final???
//공통 부모 DTO
public class CreateSimulationResponseDto {
    //기본 정보
    private Long simulationId;

    public static CreateSimulationResponseDto from(Simulation simulation) {
        return CreateSimulationResponseDto.builder()
                .simulationId(simulation.getId())
                .build();

    }
}