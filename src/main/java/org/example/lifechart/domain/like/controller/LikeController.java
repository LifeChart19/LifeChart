package org.example.lifechart.domain.like.controller;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
	private final LikeService likeService;

	// 좋아요++
	@PostMapping("/goals/{goalId}/likes")
	public ResponseEntity<ApiResponse<LikeResponseDto>> plusLike(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long goalId) {
		return ApiResponse.onSuccess(SuccessCode.CREATE_LIKE_SUCCESS,
			likeService.plusLike(customUserPrincipal.getUserId(), goalId));
	}

	// 좋아요 목록 조회 (page)
	@GetMapping("/goals/{goalId}/likes")
	public ResponseEntity<ApiResponse<Page<LikeGetResponseDto>>> getLikes(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long goalId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_LIKE_SUCCESS,
			likeService.getLikes(customUserPrincipal.getUserId(), goalId, page, size));
	}

	// 좋아요 단건 조회
	@GetMapping("/likes/{likeId}")
	public ResponseEntity<ApiResponse<LikeGetResponseDto>> getLike(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long likeId) {
		return ApiResponse.onSuccess(SuccessCode.GET_LIKE_SUCCESS,
			likeService.getLike(customUserPrincipal.getUserId(), likeId));
	}

	// 좋아요 취소
	@DeleteMapping("/likes/{likeId}")
	public ResponseEntity<ApiResponse<Void>> deleteLike(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long likeId) {
		likeService.deleteLike(customUserPrincipal.getUserId(), likeId);
		return ApiResponse.onSuccess(SuccessCode.DELETE_LIKE_SUCCESS, null);
	}

}
