package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class H2DefaultRetirementGoalServiceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DefaultRetirementGoalService defaultRetirementGoalService;

	LocalDateTime fixedNow = LocalDateTime.of(2025, 7, 1, 0, 0);

	@Test
	@DisplayName("기본 은퇴 목표를 정상적으로 생성한다.")
	void createDefaultRetirementGoal_기본_은퇴_목표를_정상적으로_생성한다() {
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

		// when
		GoalResponse response = defaultRetirementGoalService.createDefaultRetirementGoal(user.getId());

		// then
		assertThat(response.getGoalId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("유효한 유저가 아닌 경우 예외를 던진다.")
	void createDefaultRetirementGoal_유효한_유저가_아닌_경우_USER_NOT_FOUND_예외를_던진다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email1@email.com")
			.password("5678")
			.nickname("닉네임1")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.deletedAt(fixedNow)
			.build();

		userRepository.save(user);

		// when & then
		CustomException customException = assertThrows(CustomException.class, () ->
			defaultRetirementGoalService.createDefaultRetirementGoal(user.getId()));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("기대수명이 현재 나이와 같거나 작은 경우 예외를 던진다.")
	void createDefaultRetirementGoal_기대수명이_현재_나이와_같거나_작은_경우_INVALID_EXPECTED_LIFESPAN_예외를_던진다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email2@email.com")
			.password("5678")
			.nickname("닉네임2")
			.gender("male")
			.birthDate(LocalDate.of(1930,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			defaultRetirementGoalService.createDefaultRetirementGoal(user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_EXPECTED_LIFESPAN);
	}
}