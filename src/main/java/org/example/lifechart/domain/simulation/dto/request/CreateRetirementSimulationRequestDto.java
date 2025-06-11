package org.example.lifechart.domain.simulation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.lifechart.domain.goal.enums.RetirementType;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRetirementSimulationRequestDto extends BaseCreateSimulationRequestDto {

    @Schema(description = "연결할 목표 ID", example = "1")
    @NotNull
    private Long goalId;  // Goal 객체가 아니라 goalId 값만 받음

    @Schema(description = "은퇴 유형", example = "STANDARD")
    @NotNull
    //이걸 goal에서 enumtype을 갖고올 수있 을지??
    private RetirementType retirementType;

    @Schema(description = "예상 사망일", example = "2090-01-01")
    @NotNull
    private LocalDate expectedDeathDate;

    @Schema(description = "월 지출 금액", example = "2000000")
    @NotNull
    private Long monthlyExpense;

}
