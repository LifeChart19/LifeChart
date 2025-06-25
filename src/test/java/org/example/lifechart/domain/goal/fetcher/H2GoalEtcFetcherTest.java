package org.example.lifechart.domain.goal.fetcher;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalEtcInfoResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalEtcRepository;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // application-test.properties 적용
public class H2GoalEtcFetcherTest {

	@Autowired
	private GoalEtcRepository goalEtcRepository;

	@Autowired
	private GoalEtcFetcher goalEtcFetcher;

	@Autowired
	private GoalRepository goalRepository;

	LocalDateTime fixedNow = LocalDateTime.of(2025, 9, 1, 0, 0);
	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("category가 ETC이면 true를 반환한다.")
	void supports_category가_ETC이면_true를_반환한다() {
		// given
		Category category = Category.ETC;

		// when
		boolean result = goalEtcFetcher.supports(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("category가 ETC이나 DB에 없으면 예외를 던진다.")
	void fetch_category가_ETC이고_DB에_있으면_정상응답을_반환한다() {
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
			.title("세계일주 90일")
			.category(Category.ETC)
			.targetAmount(30_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(3))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("세계일주"))
			.build();

		goalRepository.save(goal);

		GoalEtc goalEtc = GoalEtc.builder()
			.goal(goal)
			.theme("여행")
			.expectedPrice(30_000_000L)
			.build();

		goalEtcRepository.save(goalEtc);
		// when
		GoalDetailInfoResponse response = goalEtcFetcher.fetch(goal.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response).isInstanceOf(GoalEtcInfoResponse.class);
	}


	@Test
	@DisplayName("category가 ETC이나 DB에 없으면 예외를 던진다.")
	void fetch_category가_ETC이나_DB에_없으면_GOAL_ETC_NOT_FOUND_예외를_던진다() {
		// given
		User user = User.builder()
			.id(1L)
			.build();

		Goal goal = Goal.builder()
			.user(user)
			.title("세계일주 90일")
			.category(Category.ETC)
			.targetAmount(30_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(3))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("세계일주"))
			.build();

		goalRepository.save(goal);

		GoalEtc goalEtc = GoalEtc.builder()
			.goal(goal)
			.theme("여행")
			.expectedPrice(30_000_000L)
			.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalEtcFetcher.fetch(goal.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_ETC_NOT_FOUND);
	}
}
