package org.example.lifechart.domain.like.controller;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.example.lifechart.domain.like.service.LikeService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LikeController {
	private final LikeService likeService;

	@PostMapping("/goals/{goalId}/likes")
	public ResponseEntity<ApiResponse<LikeResponseDto>> plusLike(@PathVariable Long goalId) {
		return ApiResponse.onSuccess(SuccessCode.CREATE_LIKE_SUCCESS, likeService.plusLike(goalId));
	}

	@GetMapping("/goals/{goalId}/likes")
	public ResponseEntity<ApiResponse<Page<LikeGetResponseDto>>> getLikes(
		@PathVariable Long goalId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_LIKE_SUCCESS,
			likeService.getLikes(goalId, page, size));
	}

	@GetMapping("/likes/{likeId}")
	public ResponseEntity<ApiResponse<LikeGetResponseDto>> getLike(@PathVariable Long likeId) {
		return ApiResponse.onSuccess(SuccessCode.GET_LIKE_SUCCESS, likeService.getLike(likeId));
	}

	@DeleteMapping("/likes/{likeId}")
	public ResponseEntity<ApiResponse<Void>> deleteLike(@PathVariable Long likeId) {
		likeService.deleteLike(likeId);
		return ApiResponse.onSuccess(SuccessCode.DELETE_LIKE_SUCCESS, null);
	}

}
