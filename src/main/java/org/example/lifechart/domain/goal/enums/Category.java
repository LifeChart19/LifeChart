package org.example.lifechart.domain.goal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
	HOUSING("주거"),
	RETIREMENT("은퇴"),
	ETC("기타"),
	TRAVEL("여행") ;

	private final String description;

}
