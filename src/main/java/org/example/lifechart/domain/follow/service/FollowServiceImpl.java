package org.example.lifechart.domain.follow.service;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowersResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowingResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowRequestResponseDto;
import org.example.lifechart.domain.follow.entity.Follow;
import org.example.lifechart.domain.follow.repository.FollowRepository;
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

	@Transactional
	@Override
	public FollowRequestResponseDto followRequest(Long userId) {
		// 로그인 유저가 있는지 검증
		// 유저가 있는지 검증
		// 자기 자신이 아닌지 검증
		// 로그인 유저 대체용
		Long myId = 1L;
		if (followRepository.existsByRequestIdAndReceiverId(myId, userId)) {
			throw new CustomException(ErrorCode.FOLLOW_CONFLICT);
		}
		Follow follow = Follow.createFollow(myId, userId);
		Follow savedFollow = followRepository.save(follow);
		return FollowRequestResponseDto.from(savedFollow);
	}

	@Transactional
	@Override
	public Page<FollowGetFollowersResponseDto> getFollowers(Long userId, int page, int size) {
		// 로그인 유저가 있는지 검증
		// 유저가 있는지 검증
		Pageable pageable = PageRequest.of(page - 1, size);
		return followRepository.findByReceiverId(userId, pageable).map(FollowGetFollowersResponseDto::from);
	}

	@Transactional
	@Override
	public Page<FollowGetFollowingResponseDto> getFollowing(Long userId, int page, int size) {
		// 로그인 유저가 있는지 검증
		// 유저가 있는지 검증
		Pageable pageable = PageRequest.of(page - 1, size);
		return followRepository.findByRequestId(userId, pageable).map(FollowGetFollowingResponseDto::from);
	}

	@Transactional
	@Override
	public FollowGetResponseDto getFollow(Long followId) {
		// 로그인 유저가 있는지 검증
		Follow findedFollow = followRepository.findById(followId)
			.orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));
		return FollowGetResponseDto.from(findedFollow);
	}

	@Transactional
	@Override
	public void followCancel(Long userId) {
		// 로그인 유저가 있는지 검증
		// 유저가 있는지 검증
		// 로그인 유저 대체용
		Long myId = 1L;
		Follow findedFollow = followRepository.findByRequestIdAndReceiverId(myId, userId)
			.orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));
		followRepository.delete(findedFollow);
	}
}
