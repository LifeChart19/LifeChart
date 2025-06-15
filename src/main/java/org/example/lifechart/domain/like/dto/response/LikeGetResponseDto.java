package org.example.lifechart.domain.like.dto.response;

import java.time.LocalDateTime;

import org.example.lifechart.domain.like.entity.Like;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeGetResponseDto {
	private Long id;
	private Long userId;
	private Long goalId;
	private LocalDateTime createdAt;

	public static LikeGetResponseDto from(Like like) {
		return LikeGetResponseDto.builder()
			.id(like.getId())
			.userId(like.getUser().getId())
			.goalId(like.getGoal().getId())
			.createdAt(like.getCreatedAt())
			.build();
	}
}
