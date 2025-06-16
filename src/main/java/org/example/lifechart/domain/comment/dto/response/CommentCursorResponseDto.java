package org.example.lifechart.domain.comment.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentCursorResponseDto {
	private List<CommentGetResponseDto> content;
	private Long nextCursor;

	public static CommentCursorResponseDto from(List<CommentGetResponseDto> content) {
		Long nextCursor = content.isEmpty() ? null : content.getLast().getId();
		return CommentCursorResponseDto.builder()
			.content(content)
			.nextCursor(nextCursor)
			.build();
	}
}
