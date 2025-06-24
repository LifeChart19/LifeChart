package org.example.lifechart.domain.shareGoal.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShareGoalCursorResponseDto {
	private List<ShareGoalResponseDto> content;
	private Long nextCursor;
	public static ShareGoalCursorResponseDto from(List<ShareGoalResponseDto> content ) {
		Long nextCursor = content.isEmpty() ? null : content.getLast().getGoalId();
		return ShareGoalCursorResponseDto.builder()
			.content(content)
			.nextCursor(nextCursor)
			.build();
	}
}
