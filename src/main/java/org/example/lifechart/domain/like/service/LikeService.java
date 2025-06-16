package org.example.lifechart.domain.like.service;

import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.springframework.data.domain.Page;

public interface LikeService {
	LikeResponseDto plusLike(Long authId, Long goalId);

	Page<LikeGetResponseDto> getLikes(Long authId, Long goalId, int page, int size);

	LikeGetResponseDto getLike(Long authId, Long likeId);

	void deleteLike(Long authId, Long likeId);
}
