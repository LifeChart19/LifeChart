package org.example.lifechart.domain.comment.controller;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.dto.response.CommentCursorResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentGetResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentResponseDto;
import org.example.lifechart.domain.comment.service.CommentService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
	public final CommentService commentService;

	// 댓글 생성
	@PostMapping("/goals/{goalId}/comments")
	public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long goalId,
		@RequestBody CommentRequestDto commentRequestDto) {
		return ApiResponse.onSuccess(SuccessCode.CREATE_COMMENT_SUCCESS,
			commentService.createComment(customUserPrincipal.getUserId(), goalId, commentRequestDto));
	}

	// 댓글 목록 조회 (cursor 기반 QueryDSL)
	@GetMapping("/goals/{goalId}/comments")
	public ResponseEntity<ApiResponse<CommentCursorResponseDto>> getComments(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long goalId,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(defaultValue = "10") int size) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_COMMENT_SUCCESS,
			commentService.getComments(customUserPrincipal.getUserId(), goalId, cursorId, size));
	}

	// 댓글 단건 조회
	@GetMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<CommentGetResponseDto>> getComment(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long commentId) {
		return ApiResponse.onSuccess(SuccessCode.GET_COMMENT_SUCCESS,
			commentService.getComment(customUserPrincipal.getUserId(), commentId));
	}

	// 댓글 수정
	@PutMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<CommentGetResponseDto>> updateComment(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long commentId,
		@RequestBody CommentRequestDto commentRequestDto) {
		return ApiResponse.onSuccess(SuccessCode.UPDATE_COMMENT_SUCCESS,
			commentService.updateComment(customUserPrincipal.getUserId(), commentId, commentRequestDto));
	}

	// 댓글 삭제
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<Void>> deleteComment(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long commentId) {
		commentService.deleteComment(customUserPrincipal.getUserId(), commentId);
		return ApiResponse.onSuccess(SuccessCode.DELETE_COMMENT_SUCCESS, null);
	}

}
