package org.example.lifechart.domain.like.dto.response;

import org.example.lifechart.domain.like.entity.Like;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponseDto {
	private Long id;

	public static LikeResponseDto from(Like like) {
		return LikeResponseDto.builder()
			.id(like.getId())
			.build();
	}

}
