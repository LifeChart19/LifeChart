package org.example.lifechart.domain.follow.service;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowersResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowingResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowRequestResponseDto;
import org.example.lifechart.domain.follow.entity.Follow;
import org.example.lifechart.domain.follow.repository.FollowRepository;
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
public class FollowServiceImpl implements FollowService {
	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	@Transactional
	@Override
	public FollowRequestResponseDto followRequest(Long authId, Long userId) {
		// 유저 검증
		User authUser = validUser(authId);
		User findUser = validUser(userId);

		// 자기 자신이 아닌지 검증
		if (authId == userId) {
			throw new CustomException(ErrorCode.FOLLOW_FORBIDDEN);
		}
		if (followRepository.existsByRequesterIdAndReceiverId(authId, userId)) {
			throw new CustomException(ErrorCode.FOLLOW_CONFLICT);
		}
		Follow follow = Follow.createFollow(authUser, findUser);
		Follow savedFollow = followRepository.save(follow);
		return FollowRequestResponseDto.from(savedFollow);
	}

	@Transactional
	@Override
	public Page<FollowGetFollowersResponseDto> getFollowers(Long authId, Long userId, int page, int size) {
		// 유저 검증
		User authUser = validUser(authId);
		User findUser = validUser(userId);
		Pageable pageable = PageRequest.of(page - 1, size);
		return followRepository.findByReceiverId(findUser.getId(), pageable).map(FollowGetFollowersResponseDto::from);
	}

	@Transactional
	@Override
	public Page<FollowGetFollowingResponseDto> getFollowing(Long authId, Long userId, int page, int size) {
		// 유저 검증
		User authUser = validUser(authId);
		User findUser = validUser(userId);
		Pageable pageable = PageRequest.of(page - 1, size);
		return followRepository.findByRequesterId(findUser.getId(), pageable).map(FollowGetFollowingResponseDto::from);
	}

	@Transactional
	@Override
	public FollowGetResponseDto getFollow(Long authId, Long followId) {
		// 유저 검증
		User authUser = validUser(authId);
		Follow foundFollow = followRepository.findById(followId)
			.orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));
		return FollowGetResponseDto.from(foundFollow);
	}

	@Transactional
	@Override
	public void followCancel(Long authId, Long userId) {
		// 유저 검증
		User authUser = validUser(authId);
		User findUser = validUser(userId);
		Follow foundFollow = followRepository.findByRequesterIdAndReceiverId(authUser.getId(), findUser.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));
		followRepository.delete(foundFollow);
	}

	private User validUser(Long userId) {
		return userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
