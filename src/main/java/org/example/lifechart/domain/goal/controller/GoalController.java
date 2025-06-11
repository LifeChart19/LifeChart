// package org.example.lifechart.domain.goal.controller;
//
// import java.security.Principal;
//
// import org.example.lifechart.common.enums.SuccessCode;
// import org.example.lifechart.common.response.ApiResponse;
// import org.example.lifechart.domain.goal.dto.request.GoalCalculateRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
// import org.example.lifechart.domain.goal.dto.response.GoalResponseDto;
// import org.example.lifechart.domain.goal.service.GoalCalculateService;
// import org.example.lifechart.domain.goal.service.GoalServiceImpl;
// import org.example.lifechart.domain.user.entity.User;
// import org.example.lifechart.domain.user.service.UserService;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
//
// @Tag(name= "Gaol", description = "목표 API")
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/goals")
// public class GoalController {
//
// 	private final GoalCalculateService goalCalculateService;
// 	private final GoalServiceImpl goalService;
// 	private final UserService userService;
//
// 	@Operation(
// 		summary = "목표 금액 계산",
// 		description = "유저의 목표, 목표 상세 입력값을 바탕으로 목표 금액을 계산합니다."
// 	)
// 	// 1. 목표 금액 계산 API (프론트에서 '적용' 버튼 클릭 시 호출)
// 	@PostMapping("/calculate")
// 	public ResponseEntity<ApiResponse<Long>> calculateTargetAmount(
// 		@RequestBody GoalCalculateRequestDto requestDto,
// 		@AuthenticationPrincipal customUserPrincipal principal) {
//
// 		User user = userService.getUserById(principal.getId()); // 메서드 학인 필요
// 		Long calculatedTargetAmount = goalCalculateService.calculateTargetAmount(requestDto, user);
//
// 		return ApiResponse.onSuccess(SuccessCode.GOAL_CALCULATE_SUCCESS, calculatedTargetAmount);
// 	};
//
// 	@Operation(
// 		summary = "목표 생성",
// 		description = "새로운 목표를 생성합니다."
// 	)
// 	@PostMapping
// 	public ResponseEntity<ApiResponse<GoalResponseDto>> createGoal(
// 		@Valid @RequestBody GoalCreateRequestDto requestDto,
// 		@AuthenticationPrincipal CustomUserPrincipal principal
// 		) {
// 		User user = userService.getUserById(principal.getId());
// 		GoalResponseDto responseDto = goalService.createGoal(requestDto, user);
// 		return ApiResponse.onSuccess(SuccessCode.GOAL_CREATE_SUCCESS, responseDto);
// 	}
//
//
//
// }
