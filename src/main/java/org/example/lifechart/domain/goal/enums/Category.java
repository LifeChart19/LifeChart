package org.example.lifechart.domain.goal.enums;

public enum Category {
	HOUSING("주거"), // 주거
	RETIREMENT("은퇴"), // 은퇴
	ETC("기타"); // 기타

	private final String description;

	Category(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
