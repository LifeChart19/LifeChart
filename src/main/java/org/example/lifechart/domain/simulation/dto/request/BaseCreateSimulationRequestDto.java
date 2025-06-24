package org.example.lifechart.domain.simulation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseCreateSimulationRequestDto {

    @Schema(description = "시뮬레이션 제목", example = "5년 뒤에 1억 모으기")
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @Schema(description = "기준일 (시뮬레이션 시작 기준 날짜)", example = "2025-06-07")
    @NotNull(message = "기준일은 필수 입력값입니다.")
    private LocalDate baseDate;

    @Schema(description = "초기 자산 (현재 보유 금액)", example = "10000000")
    @NotNull(message = "초기 자산(initialAsset)은 필수 입력값입니다.")
    @Min(value = 0, message = "초기 자산(initialAsset)은 0 이상이어야 합니다.")
    private Long initialAsset;

    //일단 월 수입과 월 지출은 현재 계산에 사용되고 있지않지만 혹시 모르니 일단 필드는 남겨두겠습니다.
    @Schema(description = "월 수입", example = "3000000")
    @NotNull(message = "월 수입(monthlyIncome)은 필수 입력값입니다.")
    @Min(value = 0, message = "월 수입(monthlyIncome)은 0 이상이어야 합니다.")
    private Long monthlyIncome;

    @Schema(description = "월 지출", example = "2000000")
    @NotNull(message = "월 지출(monthlyExpense)은 필수 입력값입니다.")
    @Min(value = 0, message = "월 지출(monthlyExpense)은 0 이상이어야 합니다.")
    private Long monthlyExpense;

    @Schema(description = "월 저축액 (월 수입 - 지출, 입력하지 않으면 자동 계산)", example = "1000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 0, message = "월 저축액(monthlySaving)은 0 이상이어야 합니다.")
    private Long monthlySaving;

    @Schema(description = "연 이율 (%) - 단리 기준", example = "6.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @DecimalMin(value = "0.0", inclusive = true, message = "연 이율(annualInterestRate)은 0 이상이어야 합니다.")
    private double annualInterestRate;

    @Schema(description = "경과 개월 수 (현재까지 저축한 기간)", example = "12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 0, message = "경과 개월 수(elapsedMonths)는 0 이상이어야 합니다.")
    private int elapsedMonths;

    @Schema(description = "총 시뮬레이션 개월 수 (예: 60개월)", example = "60", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "시뮬레이션 개월 수(totalMonths)는 1 이상이어야 합니다.")
    private int totalMonths;

//    @Schema(description = "시뮬레이션 파라미터", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
//    private SimulationParams params;

    @Schema(description = "연결할 목표 ID 리스트", example = "[1, 2, 3]")
    @NotEmpty(message = "goalIds는 하나 이상의 값을 포함해야 합니다.")
    private List<Long> goalIds = new ArrayList<>();
}
