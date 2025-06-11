package org.example.lifechart.domain.follow.service;

import org.example.lifechart.domain.follow.dto.response.FollowGetFollowersResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowingResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowRequestResponseDto;
import org.springframework.data.domain.Page;

public interface FollowService {
	FollowRequestResponseDto followRequest(Long userId);

	Page<FollowGetFollowersResponseDto> getFollowers(Long userId, int page, int size);

	Page<FollowGetFollowingResponseDto> getFollowing(Long userId, int page, int size);

	FollowGetResponseDto getFollow(Long followId);

	void followCancel(Long userId);

}
