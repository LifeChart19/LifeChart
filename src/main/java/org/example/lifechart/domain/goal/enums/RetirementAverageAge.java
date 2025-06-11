package org.example.lifechart.domain.goal.enums;

public enum RetirementAverageAge { // default 은퇴 목표 데이터 생성 시 사용
	MALE(67), // 남자 평균 은퇴 나이
	FEMALE(66); // 여자 평균 은퇴 나이

	private final int age;

	RetirementAverageAge(int age) {
		this.age = age;
	}

	public int getAge() {
		return age;
	}

	public static int getByGender(String gender) {
		return "female".equalsIgnoreCase(gender) ? FEMALE.age : MALE.age;
	}
}
