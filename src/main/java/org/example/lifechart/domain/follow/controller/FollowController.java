package org.example.lifechart.domain.follow.controller;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowersResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowingResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowRequestResponseDto;
import org.example.lifechart.domain.follow.service.FollowService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Follow", description = "팔로우 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FollowController {
	private final FollowService followService;

	@Operation(
		summary = "팔로우 요청 API",
		description = "인증된 유저가 대상 유저에 팔로우 요청을 보냅니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@PostMapping("/users/{userId}/follow")
	public ResponseEntity<ApiResponse<FollowRequestResponseDto>> followRequest(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long userId) {
		return ApiResponse.onSuccess(SuccessCode.CREATE_FOLLOW_SUCCESS, followService.followRequest(
			customUserPrincipal.getUserId(), userId));
	}

	@Operation(
		summary = "대상의 팔로워 조회 API",
		description = "인증된 유저가 대상 유저의 팔로워(팔로우 요청 보낸 유저) 목록을 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/users/{userId}/followers")
	public ResponseEntity<ApiResponse<Page<FollowGetFollowersResponseDto>>> getFollowers(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long userId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_FOLLOWERS_SUCCESS,
			followService.getFollowers(customUserPrincipal.getUserId(), userId, page, size));
	}

	@Operation(
		summary = "대상의 팔로잉 조회 API",
		description = "인증된 유저가 대상 유저가 팔로우 요청 보낸 유저 목록을 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/users/{userId}/following")
	public ResponseEntity<ApiResponse<Page<FollowGetFollowingResponseDto>>> getFollowing(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long userId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_FOLLOWING_SUCCESS,
			followService.getFollowing(customUserPrincipal.getUserId(), userId, page, size));
	}

	@Operation(
		summary = "팔로우 단건 조회 API",
		description = "인증된 유저가 팔로우 정보를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/follow/{followId}")
	public ResponseEntity<ApiResponse<FollowGetResponseDto>> getFollow(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long followId) {
		return ApiResponse.onSuccess(SuccessCode.GET_FOLLOW_SUCCESS,
			followService.getFollow(customUserPrincipal.getUserId(), followId));
	}

	@Operation(
		summary = "팔로우 취소 API",
		description = "인증된 유저가 대상 유저를 팔로우 취소합니다.",
		security = @SecurityRequirement(name="bearerAuth")
	)
	@DeleteMapping("/users/{userId}/follow")
	public ResponseEntity<ApiResponse<Void>> followCancel(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long userId) {
		followService.followCancel(customUserPrincipal.getUserId(), userId);
		return ApiResponse.onSuccess(SuccessCode.DELETE_FOLLOW_SUCCESS, null);
	}

}
