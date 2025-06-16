package org.example.lifechart.domain.goal.fetcher;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalHousingInfoResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.domain.goal.repository.GoalHousingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalHousingFetcherTest {

	@Mock
	private GoalHousingRepository goalHousingRepository;

	@InjectMocks
	private GoalHousingFetcher goalHousingFetcher;

	@Test
	@DisplayName("카테고리가 HOUSING이면 true를 반환한다.")
	void supports_목표_카테고리가_HOUSING이면_True를_반환한다() {
		// given
		Category category = Category.HOUSING;

		// when & then
		assertThat(goalHousingFetcher.supports(category)).isEqualTo(true);

	}

	@Test
	@DisplayName("카테고리가 HOUSING이 아니면 false를 반환한다.")
	void supports_목표_카테고리가_HOUSING이_아니면_false를_반환한다() {
		// given
		Category category = Category.ETC;

		// when & then
		assertThat(goalHousingFetcher.supports(category)).isEqualTo(false);
	}

	@Test
	@DisplayName("주거 목표 상세 정보를 정상적으로 반환한다")
	void fetch_주거_목표_상세_정보를_정상적으로_반환한다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.HOUSING)
			.build();

		GoalHousing goalHousing = GoalHousing.builder()
			.id(1L)
			.goal(goal)
			.region("서울")
			.subregion("서남권")
			.housingType(HousingType.APARTMENT)
			.area(100L)
			.build();

		given(goalHousingRepository.findByGoalId(goal.getId())).willReturn(Optional.of(goalHousing));

		// when
		GoalDetailInfoResponse detailInfoResponse = goalHousingFetcher.fetch(goal.getId());

		// then
		assertThat(detailInfoResponse).isInstanceOf(GoalHousingInfoResponse.class);
		GoalHousingInfoResponse housingInfoResponse = (GoalHousingInfoResponse) detailInfoResponse;

		assertThat(housingInfoResponse.getArea()).isEqualTo(100L);
		assertThat(housingInfoResponse.getRegion()).isEqualTo("서울");
		assertThat(housingInfoResponse.getSubregion()).isEqualTo("서남권");
		assertThat(housingInfoResponse.getHousingType()).isEqualTo(HousingType.APARTMENT);
	}

	@Test
	@DisplayName("주거 목표가 DB에 없으면 예외를 던진다.")
	void fetch_goalID에_해당하는_주거_목표가_없으면_예외를_던진다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.ETC)
			.build();

		given(goalHousingRepository.findByGoalId(goal.getId())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalHousingFetcher.fetch(goal.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_HOUSING_NOT_FOUND);
	}
}
