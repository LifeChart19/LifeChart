package org.example.lifechart.domain.goal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RetirementMonthlyExpense {
	COUPLE(2793000L), // 부부 평균
	SOLO(1698000L); // 개인 평균

	private final Long value;
}
