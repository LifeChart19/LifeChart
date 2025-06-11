package org.example.lifechart.domain.follow.dto.response;

import java.time.LocalDateTime;

import org.example.lifechart.domain.follow.entity.Follow;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowGetFollowersResponseDto {
	private Long id;
	private Long requestId;
	private LocalDateTime createdAt;

	public static FollowGetFollowersResponseDto from(Follow follow) {
		return FollowGetFollowersResponseDto.builder()
			.id(follow.getId())
			.requestId(follow.getRequestId())
			.createdAt(follow.getCreatedAt())
			.build();
	}
}
