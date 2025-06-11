package org.example.lifechart.domain.goal.enums;

public enum Status {
	ACTIVE("활성화"), // 활성화
	ACHIEVED("달성"), // 달성
	DELETED("삭제"), // 삭제
	MISSED("미달성"); // 미달성

	private final String description;

	Status(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}