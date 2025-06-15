package org.example.lifechart.domain.goal.controller;

import java.time.LocalDate;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalHousingCalculateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementCalculateRequest;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.dto.response.GoalRetirementEstimateResponse;
import org.example.lifechart.domain.goal.service.GoalHousingCalculateService;
import org.example.lifechart.domain.goal.service.GoalRetirementCalculateService;
import org.example.lifechart.domain.goal.service.GoalServiceImpl;
import org.example.lifechart.domain.goal.service.RetirementReferenceValueService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name= "Gaol", description = "목표 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goals")
public class GoalController {

	private final GoalRetirementCalculateService goalRetirementCalculateService;
	private final GoalHousingCalculateService goalHousingCalculateService;
	private final RetirementReferenceValueService retirementReferenceValueService;
	private final GoalServiceImpl goalService;

	@Operation(
		summary = "은퇴 목표 기본 설정값(estimate) 반환 API",
		description = "유저의 정보를 바탕으로 은퇴 목표의 기본 설정값을 반환합니다."
	)
	@GetMapping("/retirement/estimate")
	public ResponseEntity<ApiResponse<GoalRetirementEstimateResponse>> getEstimateRetirementGoal(
		@AuthenticationPrincipal CustomUserPrincipal principal
	) {
		int currentYear = LocalDate.now().getYear();
		GoalRetirementEstimateResponse response = retirementReferenceValueService.getReferenceValues(principal.getUserId(), currentYear);
		return ApiResponse.onSuccess(SuccessCode.GOAL_RETIREMENT_ESTIMATE_SUCCESS, response);
	}

	@Operation(
		summary = "은퇴 목표 금액 계산 API",
		description = "은퇴 목표 입력값을 바탕으로 목표 금액을 계산합니다."
	)
	// 은퇴 목표 금액 계산 API (값 입력 후 프론트에서 '계산' 버튼 클릭 시 호출됩니다.)
	@PostMapping("/retirement/calculate")
	public ResponseEntity<ApiResponse<Long>> calculateRetirementTargetAmount(
		@RequestBody GoalRetirementCalculateRequest request,
		@AuthenticationPrincipal CustomUserPrincipal principal
	) {
		Long calculatedTargetAmount = goalRetirementCalculateService.calculateTargetAmount(request, principal.getUserId());
		return ApiResponse.onSuccess(SuccessCode.GOAL_CALCULATE_SUCCESS, calculatedTargetAmount);
	};

	@Operation(
		summary = "주거 목표 금액 계산 API",
		description = "주거 목표 입력값을 바탕으로 목표 금액을 계산합니다."
	)
	@PostMapping("/housing/calculate")
	public ResponseEntity<ApiResponse<Long>> calculateHousingTargetAmount(
		@RequestBody GoalHousingCalculateRequest request,
		@AuthenticationPrincipal CustomUserPrincipal principal
	) {
		Long targetAmount = goalHousingCalculateService.calculateTargetAmount(request);
		return ApiResponse.onSuccess(SuccessCode.GOAL_CALCULATE_SUCCESS, targetAmount);
	}

	@Operation(
		summary = "목표 생성",
		description = "새로운 목표를 생성합니다."
	)
	@PostMapping
	public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
		@Valid @RequestBody GoalCreateRequest request,
		@AuthenticationPrincipal CustomUserPrincipal principal
		) {

		GoalResponse response = goalService.createGoal(request, principal.getUserId());
		return ApiResponse.onSuccess(SuccessCode.GOAL_CREATE_SUCCESS, response);
	}

	@Operation(
			summary = "목표 개별 조회",
			description = "개별 목표를 조회합니다."
	)
	@GetMapping("/{goalId}")
	public ResponseEntity<ApiResponse<GoalInfoResponse>> getGoalInfo(@PathVariable Long goalId) {
		GoalInfoResponse response = goalService.findGoal(goalId);
		return ApiResponse.onSuccess(SuccessCode.GOAL_GET_INFO_SUCCESS, goalService.findGoal(goalId));
	}



}
