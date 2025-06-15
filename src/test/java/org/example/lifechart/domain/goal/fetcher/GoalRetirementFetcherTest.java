package org.example.lifechart.domain.goal.fetcher;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalRetirementInfoResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.goal.repository.GoalRetirementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalRetirementFetcherTest {

	@Mock
	private GoalRetirementRepository goalRetirementRepository;

	@InjectMocks
	private GoalRetirementFetcher goalRetirementFetcher;

	@Test
	@DisplayName("카테고리가 RETIREMENT이면 true를 반환한다.")
	void supports_목표_카테고리가_RETIREMENT이면_True를_반환한다() {
		// given
		Category category = Category.HOUSING;

		// when & then
		assertThat(goalRetirementFetcher.supports(category)).isEqualTo(true);

	}

	@Test
	@DisplayName("카테고리가 RETIREMENT가 아니면 false를 반환한다.")
	void supports_목표_카테고리가_RETIREMENT가_아니면_false를_반환한다() {
		// given
		Category category = Category.ETC;

		// when & then
		assertThat(goalRetirementFetcher.supports(category)).isEqualTo(false);
	}

	@Test
	@DisplayName("은퇴 목표 상세 정보를 정상적으로 반환한다.")
	void fetch_은퇴_목표_상세_정보를_정상적으로_반환한다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.RETIREMENT)
			.build();

		GoalRetirement goalRetirement = GoalRetirement.builder()
			.id(1L)
			.goal(goal)
			.monthlyExpense(5_000_000L)
			.expectedDeathDate(LocalDate.of(2083,12,31))
			.retirementType(RetirementType.COUPLE)
			.build();

		given(goalRetirementRepository.findByGoalId(goal.getId())).willReturn(Optional.of(goalRetirement));

		// when
		GoalDetailInfoResponse goalDetailInfoResponse = goalRetirementFetcher.fetch(goal.getId());

		// then
		assertThat(goalDetailInfoResponse).isInstanceOf(GoalRetirementInfoResponse.class);

		GoalRetirementInfoResponse goalRetirementInfoResponse = (GoalRetirementInfoResponse) goalDetailInfoResponse;
		assertThat(goalRetirementInfoResponse.getExpectedDeathDate()).isEqualTo(LocalDate.of(2083,12,31));
		assertThat(goalRetirementInfoResponse.getMonthlyExpense()).isEqualTo(5_000_000L);
		assertThat(goalRetirementInfoResponse.getRetirementType()).isEqualTo(RetirementType.COUPLE);
	}

	@Test
	@DisplayName("goalId에 해당하는 은퇴 목표가 DB에 없으면 예외를 던진다.")
	void fetch_goalId에_해당하는_은퇴_목표가_없으면_예외를_던진다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.ETC)
			.build();

		given(goalRetirementRepository.findByGoalId(goal.getId())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalRetirementFetcher.fetch(goal.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_RETIREMENT_NOT_FOUND);
	}
}
