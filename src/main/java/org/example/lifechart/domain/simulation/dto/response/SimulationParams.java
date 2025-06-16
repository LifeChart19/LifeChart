package org.example.lifechart.domain.simulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
//재계산을 위한 필드들. 수정 필요.
public class SimulationParams {

    private double annualInterestRate; // 반드시 필요
    private int elapsedMonths;         // 현재 시점 반영용
    private boolean prioritizeGoals;   // 다중 목표 우선순위 반영

    private Double annualIncomeGrowthRate;
    private Double annualExpenseGrowthRate;
    private Double inflationRate;
}