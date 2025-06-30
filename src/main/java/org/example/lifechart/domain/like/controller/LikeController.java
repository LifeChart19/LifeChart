package org.example.lifechart.domain.like.controller;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.example.lifechart.domain.like.service.DistributedLockLikeService;
import org.example.lifechart.domain.like.service.LikeService;
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

@Tag(name = "Like", description = "좋아요 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
	private final LikeService likeService;
	private final DistributedLockLikeService distributedLockLikeService;

	@Operation(
		summary = "좋아요 ++ API",
		description = "인증된 유저가 목표에 좋아요를 누릅니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@PostMapping("/goals/{goalId}/likes")
	public ResponseEntity<ApiResponse<LikeResponseDto>> plusLike(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long goalId) {
		return ApiResponse.onSuccess(SuccessCode.CREATE_LIKE_SUCCESS,
			distributedLockLikeService.plusLike(customUserPrincipal.getUserId(), goalId));
	}

	@Operation(
		summary = "좋아요 목록 조회 API",
		description = "인증된 유저가 목표의 좋아요 목록을 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/goals/{goalId}/likes")
	public ResponseEntity<ApiResponse<Page<LikeGetResponseDto>>> getLikes(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long goalId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_LIKE_SUCCESS,
			likeService.getLikes(customUserPrincipal.getUserId(), goalId, page, size));
	}

	@Operation(
		summary = "좋아요 단건 조회 API",
		description = "인증된 유저가 좋아요 정보를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/likes/{likeId}")
	public ResponseEntity<ApiResponse<LikeGetResponseDto>> getLike(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long likeId) {
		return ApiResponse.onSuccess(SuccessCode.GET_LIKE_SUCCESS,
			likeService.getLike(customUserPrincipal.getUserId(), likeId));
	}

	@Operation(
		summary = "좋아요 취소 API",
		description = "인증된 유저가 좋아요를 취소합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@DeleteMapping("/likes/{likeId}")
	public ResponseEntity<ApiResponse<Void>> deleteLike(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long likeId) {
		distributedLockLikeService.deleteLike(customUserPrincipal.getUserId(), likeId);
		return ApiResponse.onSuccess(SuccessCode.DELETE_LIKE_SUCCESS, null);
	}
}
