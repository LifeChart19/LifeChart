package org.example.lifechart.domain.goal.controller;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
import org.example.lifechart.domain.goal.dto.response.GoalResponseDto;
import org.example.lifechart.domain.goal.service.GoalServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name= "Gaol", description = "목표 API")
@RestController
@RequiredArgsConstructor
public class GoalController {

	private GoalServiceImpl goalService;

	@Operation(
		summary = "목표 생성",
		description = "새로운 목표를 생성합니다."
	)

	public ResponseEntity<ApiResponse<GoalResponseDto>> createGoal(
		@Valid @RequestBody GoalCreateRequestDto requestDto
		// login 유저 정보
	) {
		GoalResponseDto responseDto = null;
		return ApiResponse.onSuccess(SuccessCode.CREATE_GOAL_SUCCESS, responseDto);
	}

}
