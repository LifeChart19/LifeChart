package org.example.lifechart.domain.follow.dto.response;

import java.time.LocalDateTime;

import org.example.lifechart.domain.follow.entity.Follow;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowGetFollowingResponseDto {
	private Long id;
	private Long receiverId;
	private LocalDateTime createdAt;

	public static FollowGetFollowingResponseDto from(Follow follow) {
		return FollowGetFollowingResponseDto.builder()
			.id(follow.getId())
			.receiverId(follow.getReceiverId())
			.createdAt(follow.getCreatedAt())
			.build();
	}
}
