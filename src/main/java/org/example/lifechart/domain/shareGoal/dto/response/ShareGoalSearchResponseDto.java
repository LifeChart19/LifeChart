package org.example.lifechart.domain.shareGoal.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ShareGoalSearchResponseDto {
	private String keyword;
	private Double score;

	public static ShareGoalSearchResponseDto of(String value, Double score) {
		return ShareGoalSearchResponseDto.builder()
			.keyword(value)
			.score(score)
			.build();
	}
}
