package org.example.lifechart.domain.goal.dto.request;

import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;

public record GoalSearchCondition(
	Long cursorId, // 커서 위치
	Integer size, // 페이지 크기
	Status status, // 필터: 목표 상태
	Category category, // 필터: 카테고리
	Share share // 필터: 공유 설정
) {}
