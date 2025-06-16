package org.example.lifechart.domain.comment.dto.response;

import org.example.lifechart.domain.comment.entity.Comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
	private Long id;

	public static CommentResponseDto from(Comment comment) {
		return CommentResponseDto.builder()
			.id(comment.getId())
			.build();
	}

}
