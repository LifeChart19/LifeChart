package org.example.lifechart.domain.comment.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentRequestDto {
	private String contents;
}
