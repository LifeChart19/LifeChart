package org.example.lifechart.domain.simulation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateSimulationRequestDto {

    @Schema(description = "연결할 목표 ID 리스트", example = "[1, 2, 3]")
    @NotEmpty(message = "goalIds는 하나 이상의 값을 포함해야 합니다.")
    private List<Long> goalIds;

}
