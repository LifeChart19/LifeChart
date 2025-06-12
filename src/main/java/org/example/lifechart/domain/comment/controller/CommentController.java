package org.example.lifechart.domain.comment.controller;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.dto.response.CommentGetResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentPageResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentResponseDto;
import org.example.lifechart.domain.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {
	public final CommentService commentService;

	// 댓글 생성
	@PostMapping("/goals/{goalId}/comments")
	public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
		@PathVariable Long goalId,
		@RequestBody CommentRequestDto commentRequestDto) {
		return ApiResponse.onSuccess(SuccessCode.CREATE_COMMENT_SUCCESS,
			commentService.createComment(goalId, commentRequestDto));
	}

	@GetMapping("/goals/{goalId}/comments")
	public ResponseEntity<ApiResponse<CommentPageResponseDto>> getComments(
		@PathVariable Long goalId,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(defaultValue = "10") int size) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_COMMENT_SUCCESS,
			commentService.getComments(goalId, cursorId, size));
	}

	@GetMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<CommentGetResponseDto>> getComment(@PathVariable Long commentId) {
		return ApiResponse.onSuccess(SuccessCode.GET_COMMENT_SUCCESS,
			commentService.getComment(commentId));
	}

	@PutMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<CommentGetResponseDto>> updateComment(
		@PathVariable Long commentId,
		@RequestBody CommentRequestDto commentRequestDto) {
		return ApiResponse.onSuccess(SuccessCode.UPDATE_COMMENT_SUCCESS,
			commentService.updateComment(commentId, commentRequestDto));
	}

	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<Void>> deleteComment(
		@PathVariable Long commentId) {
		commentService.deleteComment(commentId);
		return ApiResponse.onSuccess(SuccessCode.DELETE_COMMENT_SUCCESS, null);
	}

}
