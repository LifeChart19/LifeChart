package org.example.lifechart.domain.goal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Share {
	ALL("전체공개"), // 전체공개
	FOLLOWER("팔로워 공개"), // 팔로워 공개
	PRIVATE("비공개"); // 비공개

	private final String description;
}