package org.example.lifechart.domain.goal.fetcher;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalDetailFetcherFactoryTest {

	@Mock
	private GoalHousingFetcher goalHousingFetcher;

	@Mock
	private GoalRetirementFetcher goalRetirementFetcher;

	@Mock
	private GoalEtcFetcher goalEtcFetcher;

	@Mock
	GoalDetailInfoResponse mockResponse;

	private GoalDetailFetcherFactory goalDetailFetcherFactory;

	@BeforeEach
	void setup() {
		goalDetailFetcherFactory = new GoalDetailFetcherFactory(List.of(goalEtcFetcher, goalHousingFetcher, goalRetirementFetcher));
	}

	@Test
	@DisplayName("지원하는 fetcher가 존재하면 해당 fetcher가 선택되고 fetcher가 호출된다.")
	void getDetail_지원하는_fetcher가_존재하면_fetcher가_정상적으로_호출된다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.HOUSING)
			.build();


		given(goalHousingFetcher.supports(Category.HOUSING)).willReturn(true);
		given(goalHousingFetcher.fetch(1L)).willReturn(mockResponse);

		// when
		GoalDetailInfoResponse goalDetailInfoResponse = goalDetailFetcherFactory.getDetail(goal);

		// then
		assertThat(goalDetailInfoResponse).isEqualTo(mockResponse);
		verify(goalHousingFetcher).fetch(1L);
		verify(goalRetirementFetcher, never()).fetch(any());
	}

	@Test
	@DisplayName("fetcher가 존재하지 않으면 예외를 던진다.")
	void getDetail_fecther가_존재하지_않으면_예외를_던진다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.RETIREMENT)
			.build();

		given(goalEtcFetcher.supports(Category.RETIREMENT)).willReturn(false);
		given(goalHousingFetcher.supports(Category.RETIREMENT)).willReturn(false);
		given(goalRetirementFetcher.supports(Category.RETIREMENT)).willReturn(false);

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalDetailFetcherFactory.getDetail(goal));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_INVALID_CATEGORY);
	}

}
