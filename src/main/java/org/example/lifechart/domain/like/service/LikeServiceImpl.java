package org.example.lifechart.domain.like.service;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.example.lifechart.domain.like.entity.Like;
import org.example.lifechart.domain.like.repository.LikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
	private final LikeRepository likeRepository;

	@Transactional
	@Override
	public LikeResponseDto plusLike(Long goalId) {
		// 로그인 유저 존재 여부 검증
		// 목표 존재 여부 검증
		// 로그인 대체용
		Long me = 1L;
		if (likeRepository.existsByUserIdAndGoalId(me, goalId)) {
			throw new CustomException(ErrorCode.LIKE_CONFLICT);
		}
		Like like = Like.createLike(me, goalId);
		Like savedLike = likeRepository.save(like);
		return LikeResponseDto.from(savedLike);
	}

	@Transactional
	@Override
	public Page<LikeGetResponseDto> getLikes(Long goalId, int page, int size) {
		// 로그인 유저 존재 여부 검증
		// 목표 존재 여부 검증
		Pageable pageable = PageRequest.of(page - 1, size);
		return likeRepository.findByGoalId(goalId, pageable).map(LikeGetResponseDto::from);
	}

	@Transactional
	@Override
	public LikeGetResponseDto getLike(Long likeId) {
		// 로그인 유저 존재 여부 검증
		Like findedLike = likeRepository.findById(likeId)
			.orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));
		return LikeGetResponseDto.from(findedLike);
	}

	@Transactional
	@Override
	public void deleteLike(Long likeId) {
		// 로그인 유저 존재 여부 검증
		// 로그인 대체용
		Long me = 1L;
		Like findedLike = likeRepository.findById(likeId)
			.orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));
		if (me != findedLike.getUserId()) {
			throw new CustomException(ErrorCode.LIKE_FORBIDDEN);
		}
		likeRepository.delete(findedLike);
	}

}
