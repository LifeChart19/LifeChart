package org.example.lifechart.domain.goal.helper;

import java.time.LocalDate;

public class GoalDateHelper {

	private GoalDateHelper() {
		throw new UnsupportedOperationException("정적 메서드 클래스");
	}

	public static LocalDate toExpectedDeathDate(Long expectedLifespan, int birthYear) {
		return LocalDate.of(birthYear + expectedLifespan.intValue(), 12, 31);// 12월 31일로 고정
	}
}
