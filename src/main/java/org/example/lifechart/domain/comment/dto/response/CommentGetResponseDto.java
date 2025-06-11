package org.example.lifechart.domain.comment.dto.response;

import java.time.LocalDateTime;

import org.example.lifechart.domain.comment.entity.Comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentGetResponseDto {
	private Long id;
	private Long userId;
	private Long goalId;
	private String contents;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static CommentGetResponseDto from(Comment comment) {
		return CommentGetResponseDto.builder()
			.id(comment.getId())
			.userId(comment.getUserId())
			.goalId(comment.getGoalId())
			.contents(comment.getContents())
			.createdAt(comment.getCreatedAt())
			.updatedAt(comment.getUpdatedAt())
			.build();
	}
}
