package org.example.lifechart.domain.simulation.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseCreateSimulationRequestDto {

    @Schema(description = "제목", example = "5년 뒤에 1억 모으기")
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @Schema(description = "기준일", example = "2025-06-07T00:00:00")
    @NotNull(message = "기준일은 필수 입력값입니다.")
    private LocalDateTime baseDate;

    @Schema(description = "최초 자산", example = "10000000")
    @NotNull(message = "초기 자산(initialAsset)은 필수 입력값입니다.")
    @Min(value = 0, message = "초기 자산(initialAsset)은 0 이상이어야 합니다.")
    private Long initialAsset;

    @Schema(description = "월 수입", example = "3000000")
    @NotNull(message = "월 수입(monthlyIncome)은 필수 입력값입니다.")
    @Min(value = 0, message = "월 수입(monthlyIncome)은 0 이상이어야 합니다.")
    private Long monthlyIncome;

    @Schema(description = "월 지출", example = "2000000")
    @NotNull(message = "월 지출(monthlyExpense)은 필수 입력값입니다.")
    @Min(value = 0, message = "월 지출(monthlyExpense)은 0 이상이어야 합니다.")
    private Long monthlyExpense;

//    @Schema(description = "시뮬레이션 파라미터")
//    private SimulationParams params;

    @Schema(description = "연결할 목표 ID 리스트", example = "[1, 2, 3]")
    private List<Long> goalIds = new ArrayList<>();
}

