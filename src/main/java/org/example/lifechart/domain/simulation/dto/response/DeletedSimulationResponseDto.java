package org.example.lifechart.domain.simulation.dto.response;

import lombok.*;
import org.example.lifechart.domain.simulation.entity.Simulation;

import java.time.LocalDateTime;

//소프트 딜리트된 목록을 조회할 때 필요한 dto
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class DeletedSimulationResponseDto {

    private Long simulationId;
    private String title;
    private LocalDateTime deletedAt;

    //초기 false로.
    @Builder.Default
    private boolean isDeleted = false;

    public static DeletedSimulationResponseDto toDto(Simulation simulation) {
        return DeletedSimulationResponseDto.builder()
                .simulationId(simulation.getId())
                .title(simulation.getTitle())
                .deletedAt(simulation.getDeletedAt())
                .isDeleted(true)
                .build();
    }
}