package org.example.lifechart.domain.like.service;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.example.lifechart.domain.like.entity.Like;
import org.example.lifechart.domain.like.repository.LikeRepository;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
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
	private final UserRepository userRepository;
	private final GoalRepository goalRepository;

	@Transactional
	@Override
	public LikeResponseDto plusLike(Long authId, Long goalId) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		// 목표 존재 여부 검증
		Goal findedGoal = validGoal(goalId);
		if (likeRepository.existsByUserIdAndGoalId(findedUser.getId(), findedGoal.getId())) {
			throw new CustomException(ErrorCode.LIKE_CONFLICT);
		}
		Like like = Like.createLike(findedUser, findedGoal);
		Like savedLike = likeRepository.save(like);
		return LikeResponseDto.from(savedLike);
	}

	@Transactional
	@Override
	public Page<LikeGetResponseDto> getLikes(Long authId, Long goalId, int page, int size) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		// 목표 존재 여부 검증
		Goal findedGoal = validGoal(goalId);
		Pageable pageable = PageRequest.of(page - 1, size);
		return likeRepository.findByGoalId(findedGoal.getId(), pageable).map(LikeGetResponseDto::from);
	}

	@Transactional
	@Override
	public LikeGetResponseDto getLike(Long authId, Long likeId) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		Like findedLike = likeRepository.findById(likeId)
			.orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));
		return LikeGetResponseDto.from(findedLike);
	}

	@Transactional
	@Override
	public void deleteLike(Long authId, Long likeId) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		Like findedLike = likeRepository.findById(likeId)
			.orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));
		if (findedUser.getId() != findedLike.getUser().getId()) {
			throw new CustomException(ErrorCode.LIKE_FORBIDDEN);
		}
		likeRepository.delete(findedLike);
	}

	private User validUser(Long userId) {
		return userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	private Goal validGoal(Long goalId) {
		return goalRepository.findByIdAndStatus(goalId, Status.ACTIVE)
			.orElseThrow(()-> new CustomException(ErrorCode.GOAL_NOT_FOUND));
	}

}
