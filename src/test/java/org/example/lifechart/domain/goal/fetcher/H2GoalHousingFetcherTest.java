package org.example.lifechart.domain.goal.fetcher;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalHousingInfoResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalHousingRepository;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class H2GoalHousingFetcherTest {

	@Autowired
	private GoalHousingRepository goalHousingRepository;

	@Autowired
	private GoalRepository goalRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GoalHousingFetcher goalHousingFetcher;

	LocalDateTime fixedNow = LocalDateTime.of(2025, 9, 1, 0, 0);

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
		User user = User.builder()
			.name("이름")
			.email("email@email.com")
			.password("5678")
			.nickname("닉네임")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		Goal goal = Goal.builder()
			.user(user)
			.title("강남 집 사기")
			.category(Category.HOUSING)
			.targetAmount(100_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(3))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("강남", "집"))
			.build();

		goalRepository.save(goal);

		GoalHousing goalHousing = GoalHousing.builder()
			.goal(goal)
			.region("서울")
			.subregion("도심")
			.area(100L)
			.housingType(HousingType.APARTMENT)
			.build();

		goalHousingRepository.save(goalHousing);

		// when
		GoalDetailInfoResponse response = goalHousingFetcher.fetch(goal.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response).isInstanceOf(GoalHousingInfoResponse.class);
	}

	@Test
	@DisplayName("주거 목표가 DB에 없으면 예외를 던진다.")
	void fetch_goalID에_해당하는_주거_목표가_없으면_GOAL_HOUSING_NOT_FOUND_예외를_던진다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email1@email.com")
			.password("5678")
			.nickname("닉네임1")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		Goal goal = Goal.builder()
			.user(user)
			.title("강남 집 사기")
			.category(Category.HOUSING)
			.targetAmount(100_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(3))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("강남", "집"))
			.build();

		goalRepository.save(goal);

		GoalHousing goalHousing = GoalHousing.builder()
			.goal(goal)
			.region("서울")
			.subregion("도심")
			.area(100L)
			.housingType(HousingType.APARTMENT)
			.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalHousingFetcher.fetch(goal.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_HOUSING_NOT_FOUND);
	}
}
