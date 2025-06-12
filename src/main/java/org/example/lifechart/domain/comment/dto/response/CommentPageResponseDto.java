package org.example.lifechart.domain.comment.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentPageResponseDto {
	private List<CommentGetResponseDto> content;
	private Long nextCursor;

}
