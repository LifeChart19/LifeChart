package org.example.lifechart.domain.simulation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;

import java.time.LocalDate;
import java.util.List;

//현재 계산로직을 그냥 주고 있어서 나중에 params에 넣은 값으로 요청하고 반환되는 방식으로 변경할 예정.
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//재계산을 위한 필드들. 수정 필요.
public class SimulationParams {

    @Schema(description = "시뮬레이션 제목", example = "5년 뒤에 1억 모으기")
    private String title;

    @Schema(description = "기준일 (시뮬레이션 시작 기준 날짜)", example = "2025-06-07")
    private LocalDate baseDate;

    @Schema(description = "초기 자산 (현재 보유 금액)", example = "10000000")
    private Long initialAsset;

    @Schema(description = "월 수입", example = "3000000")
    private Long monthlyIncome;

    @Schema(description = "월 지출", example = "2000000")
    private Long monthlyExpense;

    @Schema(description = "월 저축액 (월 수입 - 지출)", example = "1000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long monthlySaving;

    @Schema(description = "연 이율 (%) - 단리 기준", example = "6.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private double annualInterestRate;

    @Schema(description = "경과 개월 수 (현재까지 저축한 기간)", example = "12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private int elapsedMonths;

    @Schema(description = "총 시뮬레이션 개월 수 (예: 60개월)", example = "60", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private int totalMonths;

    @Schema(description = "연결할 목표 ID 리스트", example = "[1, 2, 3]")
    private List<Long> goalIds;

    public static SimulationParams from(BaseCreateSimulationRequestDto dto) {
        return SimulationParams.builder()
                .initialAsset(dto.getInitialAsset())
                .monthlyIncome(dto.getMonthlyIncome())
                .monthlyExpense(dto.getMonthlyExpense())
                .monthlySaving(dto.getMonthlySaving())
                .annualInterestRate(dto.getAnnualInterestRate())
                .elapsedMonths(dto.getElapsedMonths())
                .totalMonths(dto.getTotalMonths())
                .baseDate(dto.getBaseDate())
                .goalIds(dto.getGoalIds())
                .build();
    }

}