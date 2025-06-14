package org.example.lifechart.domain.goal.dto.response;

import org.example.lifechart.domain.goal.enums.RetirementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalRetirementEstimateResponse {
	private Long expectedLifespan;
	private Long monthlyExpense;
	private RetirementType retirementType;
}
