package org.example.lifechart.domain.goal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HousingType {
	APARTMENT("아파트"),
	VILA("연립/다세대주택");

	private final String description;
}
