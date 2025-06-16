package org.example.lifechart.domain.follow.dto.response;

import java.time.LocalDateTime;

import org.example.lifechart.domain.follow.entity.Follow;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowGetFollowersResponseDto {
	private Long id;
	private Long requesterId;
	private LocalDateTime createdAt;

	public static FollowGetFollowersResponseDto from(Follow follow) {
		return FollowGetFollowersResponseDto.builder()
			.id(follow.getId())
			.requesterId(follow.getRequester().getId())
			.createdAt(follow.getCreatedAt())
			.build();
	}
}
