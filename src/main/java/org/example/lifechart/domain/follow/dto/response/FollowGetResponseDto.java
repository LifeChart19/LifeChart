package org.example.lifechart.domain.follow.dto.response;

import java.time.LocalDateTime;

import org.example.lifechart.domain.follow.entity.Follow;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowGetResponseDto {
	private Long id;
	private Long requesterId;
	private Long receiverId;
	private LocalDateTime createdAt;

	public static FollowGetResponseDto from(Follow follow) {
		return FollowGetResponseDto.builder()
			.id(follow.getId())
			.requesterId(follow.getRequester().getId())
			.receiverId(follow.getReceiver().getId())
			.createdAt(follow.getCreatedAt())
			.build();
	}
}
