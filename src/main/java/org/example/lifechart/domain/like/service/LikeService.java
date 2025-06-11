package org.example.lifechart.domain.like.service;

import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.springframework.data.domain.Page;

public interface LikeService {
	LikeResponseDto plusLike(Long goalId);

	Page<LikeGetResponseDto> getLikes(Long goalId, int page, int size);

	LikeGetResponseDto getLike(Long likeId);

	void deleteLike(Long likeId);
}
