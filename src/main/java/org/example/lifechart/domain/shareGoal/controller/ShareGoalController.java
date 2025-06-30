package org.example.lifechart.domain.shareGoal.controller;

import java.util.List;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.shareGoal.dto.reqeust.ShareGoalSearchRequestDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalSearchResponseDto;
import org.example.lifechart.domain.shareGoal.service.ShareGoalService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ShareGoal", description = "공유 목표 API")
@RestController
@RequestMapping("/api/share-goals")
@RequiredArgsConstructor
public class ShareGoalController {
	private final ShareGoalService shareGoalService;

	@Operation(
		summary = "공유 목표 조회 API",
		description = "인증된 유저가 공유 목표를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
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

	@Operation(
		summary = "특정 유저의 공유 목표 조회 API",
		description = "인증된 유저가 특정 유저의 공유 목표를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/to-user")
	public ResponseEntity<ApiResponse<List<ShareGoalResponseDto>>> getShareGoalsToUser(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@RequestParam Long userId
	) {
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_SHAREGOALS_SUCCESS,
			shareGoalService.getShareGoalsToUser(customUserPrincipal.getUserId(), userId));
	}

	@Operation(
		summary = "검색을 통한 공유 목표 조회 API",
		description = "인증된 유저가 검색어를 기반으로 공유 목표를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<ShareGoalCursorResponseDto>> searchShareGoals(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam String keyword
	) {
		if (keyword.trim().length() < 2) {
			throw new CustomException(ErrorCode.SHARE_GOAL_KEYWORD_BAD_REQUEST);
		}
		return ApiResponse.onSuccess(SuccessCode.GET_ALL_SHAREGOALS_SUCCESS,
			shareGoalService.searchShareGoals(customUserPrincipal.getUserId(), cursorId, size, keyword));
	}

	@Operation(
		summary = "키워드 점수 증가 API",
		description = "인증된 유저가 검색된 키워드 점수를 증가시킵니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@PostMapping("/search")
	public ResponseEntity<ApiResponse<Void>> plusSearchKeyword(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@RequestBody ShareGoalSearchRequestDto shareGoalSearchRequestDto
	) {
		shareGoalService.plusSearchKeyword(customUserPrincipal.getUserId(), shareGoalSearchRequestDto);
		return ApiResponse.onSuccess(SuccessCode.KEYWORD_SCORE_PLUS_SUCCESS, null);
	}

	@Operation(
		summary = "인기 검색어 Top 10 조회 API",
		description = "인증된 유저가 인기 검색어 Top 10을 조회합니다",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/search/top10")
	public ResponseEntity<ApiResponse<List<ShareGoalSearchResponseDto>>> searchTop10Keyword(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
	) {
		return ApiResponse.onSuccess(SuccessCode.GET_POPULAR_KEYWORDS_SUCCESS,
			shareGoalService.searchTop10Keyword(customUserPrincipal.getUserId()));
	}
}
