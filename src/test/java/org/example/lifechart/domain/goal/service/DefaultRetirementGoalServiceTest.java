package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementCalculateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequest;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.dto.response.GoalRetirementEstimateResponse;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultRetirementGoalServiceTest {

	@Mock
	private GoalService goalService;

	@Mock
	private RetirementReferenceValueService retirementReferenceValueService;

	@Mock
	private GoalRetirementCalculateService goalRetirementCalculateService;

	@InjectMocks
	private DefaultRetirementGoalService defaultRetirementGoalService;

	LocalDateTime fixedNow = LocalDateTime.of(2025, 7, 1, 0, 0);

	@Test
	@DisplayName("기본 은퇴 목표를 정상적으로 생성한다.")
	void createDefaultRetirementGoal_기본_은퇴_목표를_정상적으로_생성한다() {
		// given
		Long userId = 1L;
		LocalDateTime startAt = fixedNow;

		GoalRetirementEstimateResponse retirementResponse = GoalRetirementEstimateResponse.builder()
			.expectedRetirementDate(LocalDateTime.of(2050,1,1,0,0))
			.expectedLifespan(90L)
			.monthlyExpense(5_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		Long targetAmount = 1_500_000_000L;

		GoalResponse dummyResponse = GoalResponse.builder()
			.goalId(1L)
			.build();

		given(retirementReferenceValueService.getReferenceValues(eq(userId), anyInt())).willReturn(retirementResponse);
		given(goalRetirementCalculateService.calculateTargetAmount(any(GoalRetirementCalculateRequest.class), eq(userId))).willReturn(targetAmount);
		given(goalService.createGoal(any(GoalCreateRequest.class), eq(userId))).willReturn(dummyResponse);

		// when
		GoalResponse response = defaultRetirementGoalService.createDefaultRetirementGoal(userId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getGoalId()).isEqualTo(1L);

		ArgumentCaptor<GoalCreateRequest> captor = ArgumentCaptor.forClass(GoalCreateRequest.class);
		verify(goalService).createGoal(captor.capture(), eq(userId));
		GoalCreateRequest req = captor.getValue();

		assertThat(req.getTitle()).isEqualTo("기본 설정 은퇴 목표");
		assertThat(req.getCategory()).isEqualTo(Category.RETIREMENT);
		assertThat(req.getTargetAmount()).isEqualTo(1_500_000_000L);
		assertThat(req.getDetail()).isInstanceOf(GoalRetirementRequest.class);
	}

	@Test
	@DisplayName("유효한 유저가 아닌 경우 예외를 던진다.")
	void createDefaultRetirementGoal_유효한_유저가_아닌_경우_USER_NOT_FOUND_예외를_던진다() {
		// given
		Long userId = 1L;

		given(retirementReferenceValueService.getReferenceValues(eq(userId), anyInt()))
			.willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		// when & then
		CustomException customException = assertThrows(CustomException.class, () ->
			defaultRetirementGoalService.createDefaultRetirementGoal(userId));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
		verify(goalRetirementCalculateService, never()).calculateTargetAmount(any(), anyLong());
		verify(goalService, never()).createGoal(any(), anyLong());
	}

	@Test
	@DisplayName("기대수명이 현재 나이와 같거나 작은 경우 예외를 던진다.")
	void createDefaultRetirementGoal_기대수명이_현재_나이와_같거나_작은_경우_INVALID_EXPECTED_LIFESPAN_예외를_던진다() {
		// given
		User user = User.builder()
			.id(1L)
			.birthDate(LocalDate.of(1990,1,1))
			.build();

		GoalRetirementEstimateResponse retirementResponse = GoalRetirementEstimateResponse.builder()
			.expectedRetirementDate(LocalDateTime.of(2050,1,1,0,0))
			.expectedLifespan(30L)
			.monthlyExpense(5_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		given(retirementReferenceValueService.getReferenceValues(user.getId(), fixedNow.getYear()))
			.willReturn(retirementResponse);

		given(goalRetirementCalculateService.calculateTargetAmount(any(GoalRetirementCalculateRequest.class), eq(user.getId())))
			.willThrow(new CustomException(ErrorCode.INVALID_EXPECTED_LIFESPAN));

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			defaultRetirementGoalService.createDefaultRetirementGoal(user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_EXPECTED_LIFESPAN);
	}

	@Test
	@DisplayName("기대수명이 종료일 이전인 경우 예외를 던진다.")
	void createDefaultRetirementGoal_기대수명이_종료일_이전인_경우_GOAL_RETIREMENT_LIFESPAN_BEFORE_END_DATE_예외를_던진다() {
		// given
		User user = User.builder()
			.id(1L)
			.birthDate(LocalDate.of(1990,1,1))
			.build();

		GoalRetirementEstimateResponse retirementResponse = GoalRetirementEstimateResponse.builder()
			.expectedRetirementDate(LocalDateTime.of(2050,1,1,0,0))
			.expectedLifespan(50L)
			.monthlyExpense(5_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		given(retirementReferenceValueService.getReferenceValues(user.getId(), fixedNow.getYear()))
			.willReturn(retirementResponse);

		given(goalRetirementCalculateService.calculateTargetAmount(any(GoalRetirementCalculateRequest.class), eq(user.getId())))
			.willThrow(new CustomException(ErrorCode.GOAL_RETIREMENT_LIFESPAN_BEFORE_END_DATE));

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			defaultRetirementGoalService.createDefaultRetirementGoal(user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_RETIREMENT_LIFESPAN_BEFORE_END_DATE);
	}
}