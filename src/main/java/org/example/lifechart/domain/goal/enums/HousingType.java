package org.example.lifechart.domain.goal.enums;

public enum HousingType {
	APARTMENT("아파트"), // 아파트
	VILA("연립/다세대주택"); // 연립/다세대주택

	private final String description;

	HousingType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
