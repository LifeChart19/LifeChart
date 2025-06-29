package org.example.lifechart.domain.goal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
	ACTIVE("활성화"), // 활성화
	ACHIEVED("달성"), // 달성
	DELETED("삭제"), // 삭제
	MISSED("미달성"); // 미달성

	private final String description;
}