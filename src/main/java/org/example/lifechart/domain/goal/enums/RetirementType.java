package org.example.lifechart.domain.goal.enums;

public enum RetirementType {
	COUPLE("부부"), // 부부
	SOLO("개인"); // 개인

	private final String description;

	RetirementType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
