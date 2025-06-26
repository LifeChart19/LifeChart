package org.example.lifechart.domain.goal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RetirementAverageAge { // default 은퇴 목표 데이터 생성 시 사용
	MALE(67), // 남자 평균 은퇴 나이
	FEMALE(66); // 여자 평균 은퇴 나이

	private final int age;

	public static int getByGender(String gender) {
		return "female".equalsIgnoreCase(gender) ? FEMALE.age : MALE.age;
	}
}
