package org.example.lifechart.domain.follow.dto.response;

import java.time.LocalDateTime;

import org.example.lifechart.domain.follow.entity.Follow;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowGetResponseDto {
	private Long id;
	private Long requestId;
	private Long receiverId;
	private LocalDateTime createdAt;

	public static FollowGetResponseDto from(Follow follow) {
		return FollowGetResponseDto.builder()
			.id(follow.getId())
			.requestId(follow.getRequestId())
			.receiverId(follow.getReceiverId())
			.createdAt(follow.getCreatedAt())
			.build();
	}
}
