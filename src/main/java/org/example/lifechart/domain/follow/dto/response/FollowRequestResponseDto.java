package org.example.lifechart.domain.follow.dto.response;

import org.example.lifechart.domain.follow.entity.Follow;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowRequestResponseDto {
	private Long id;

	public static FollowRequestResponseDto from(Follow follow) {
		return FollowRequestResponseDto.builder()
			.id(follow.getId())
			.build();
	}

}
