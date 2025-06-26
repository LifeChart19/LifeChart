package org.example.lifechart.domain.goal.fetcher;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test") // application-test.properties 적용
public class H2GoalDetailFetcherFactoryTest {

	@Autowired
	private GoalDetailFetcherFactory goalDetailFetcherFactory;

	@Autowired
	private GoalHousingRepository goalHousingRepository;

	@Autowired
	private GoalRepository goalRepository;

	@Autowired
	private UserRepository userRepository;

	LocalDateTime fixedNow = LocalDateTime.of(2025, 9, 1, 0, 0);

	@Test
	@DisplayName("지원하는 fetcher가 존재하면 해당 fetcher가 선택되고 fetcher가 호출된다.")
	@Transactional
	void getDetail_지원하는_fetcher가_존재하면_fetcher가_정상적으로_호출된다() {
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
		GoalDetailInfoResponse response = goalDetailFetcherFactory.getDetail(goal);

		// then
		assertThat(response).isNotNull();
		assertThat(response).isInstanceOf(GoalHousingInfoResponse.class);
		assertThat(((GoalHousingInfoResponse) response).getArea()).isEqualTo(100L);
	}

	@Test
	@DisplayName("fetcher가 존재하지 않으면 예외를 던진다.")
	@Transactional
	void getDetail_fecther가_존재하지_않으면_GOAL_INVALID_CATEGORY_예외를_던진다() {
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
			.id(1L)
			.user(user)
			.title("강남 집 사기")
			.category(Category.TRAVEL)
			.targetAmount(100_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(3))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("강남", "집"))
			.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalDetailFetcherFactory.getDetail(goal));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_INVALID_CATEGORY);
	}
}
