package org.example.lifechart.domain.goal.dto.request;

import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.enums.RetirementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalRetirementCalculateRequest {
	private LocalDateTime endAt;
	private Long expectedLifespan;
	private Long monthlyExpense;
	private RetirementType retirementType;
}