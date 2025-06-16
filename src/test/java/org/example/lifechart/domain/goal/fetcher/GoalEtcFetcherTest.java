package org.example.lifechart.domain.goal.fetcher;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalEtcInfoResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.repository.GoalEtcRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalEtcFetcherTest {

	@Mock
	private GoalEtcRepository goalEtcRepository;

	@InjectMocks
	private GoalEtcFetcher goalEtcFetcher;

	@Test
	@DisplayName("카테고리가 ETC면 true를 반환한다.")
	void supports_목표_카테고리가_ETC면_True를_반환한다() {
		// given
		Category category = Category.ETC;

		// when & then
		assertThat(goalEtcFetcher.supports(category)).isEqualTo(true);

	}

	@Test
	@DisplayName("카테고리가 ETC가 아니면 false를 반환한다.")
	void supports_목표_카테고리가_ETC가_아니면_false를_반환한다() {
		// given
		Category category = Category.HOUSING;

		// when & then
		assertThat(goalEtcFetcher.supports(category)).isEqualTo(false);
	}

	@Test
	@DisplayName("기타 목표의 상세 Response를 정상적으로 반환한다.")
	void fetch_목표_기타_목표의_상세_Response를_정상적으로_반환한다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.ETC)
			.build();

		GoalEtc goalEtc = GoalEtc.builder()
			.id(1L)
			.goal(goal)
			.theme("자동차")
			.expectedPrice(100_000_000L)
			.build();

		given(goalEtcRepository.findByGoalId(goal.getId())).willReturn(Optional.of(goalEtc));

		// when
		GoalDetailInfoResponse goalDetailInfoResponse = goalEtcFetcher.fetch(goal.getId());

		// then
		assertThat(goalDetailInfoResponse).isInstanceOf(GoalEtcInfoResponse.class);
		GoalEtcInfoResponse goalEtcInfoResponse = (GoalEtcInfoResponse) goalDetailInfoResponse;
		assertThat(goalEtcInfoResponse.getExpectedPrice()).isEqualTo(100_000_000L);
		assertThat(goalEtcInfoResponse.getTheme()).isEqualTo("자동차");
	}

	@Test
	@DisplayName("goalId에 해당하는 기타목표가 없으면 예외를 던진다.")
	void fetch_goalID에_해당하는_기타목표가_DB에_없으면_예외를_던진다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.HOUSING)
			.build();

		GoalEtc goalEtc = GoalEtc.builder()
			.id(1L)
			.goal(goal)
			.theme("자동차")
			.expectedPrice(100_000_000L)
			.build();

		given(goalEtcRepository.findByGoalId(goal.getId())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class, ()->
			goalEtcFetcher.fetch(goal.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_ETC_NOT_FOUND);
	}
}
