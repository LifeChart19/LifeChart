package org.example.lifechart.domain.goal.enums;

public enum Share {
	ALL("전체공개"), // 전체공개
	FOLLOWER("팔로워 공개"), // 팔로워 공개
	PRIVATE("비공개"); // 비공개

	private final String description;

	Share(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}