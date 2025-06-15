package org.example.lifechart.domain.shareGoal.controller;

import java.util.List;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;
import org.example.lifechart.domain.shareGoal.service.ShareGoalService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/share-goals")
@RequiredArgsConstructor
public class ShareGoalController {
	private final ShareGoalService shareGoalService;

	// 공유 목표 조회
	@GetMapping
	public ResponseEntity<ApiResponse<ShareGoalCursorResponseDto>> getShareGoals(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) Category category,
		@RequestParam(required = false) Share share
	) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_SHAREGOALS_SUCCESS,
			shareGoalService.getShareGoals(customUserPrincipal.getUserId(), cursorId, size, category, share));
	}

	// 특정 유저 공유 목표 조회
	@GetMapping("/to-user")
	public ResponseEntity<ApiResponse<List<ShareGoalResponseDto>>> getShareGoalsToUser(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@RequestParam Long userId
	) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_SHAREGOALS_SUCCESS,
			shareGoalService.getShareGoalsToUser(customUserPrincipal.getUserId(), userId));
	}
}
