package org.example.lifechart.domain.simulation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.domain.simulation.entity.SimulationParams;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateHouseSimulationRequestDto extends BaseCreateSimulationRequestDto {

    protected CreateHouseSimulationRequestDto(String title, LocalDateTime baseDate, Long initialAsset, Long monthlyIncome, Long monthlyExpense, SimulationParams params, List<Long> goalIds) {
        super(title, baseDate, initialAsset, monthlyIncome, monthlyExpense, params, goalIds);
    }

    @Schema(description = "연결할 목표 ID", example = "1")
    @NotNull
    private Long goalId;  // goal 객체가 아니라 goalId (FK 값만 전달)

    @Schema(description = "지역", example = "서울특별시")
    @NotBlank
    private String region;

    @Schema(description = "하위 지역", example = "강남구")
    @NotBlank
    private String subregion;

    @Schema(description = "면적 (제곱미터)", example = "85")
    @NotNull
    private Long area;

    @Schema(description = "주거 형태", example = "APARTMENT")
    @NotNull
    private HousingType housingType;

}