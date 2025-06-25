package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementCalculateRequest;
import org.example.lifechart.domain.goal.enums.RetirementType;
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
public class H2GoalRetirementCalculateServiceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GoalRetirementCalculateService goalRetirementCalculateService;

	LocalDateTime fixedNow = LocalDateTime.of(2025, 9, 1, 0, 0);

	@Test
	@DisplayName("예상 은퇴 비용을 정상적으로 계산한다.")
	void calculateTargetAmout_예상_은퇴_비용을_정상적으로_계산한다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email@email.com")
			.password("5678")
			.nickname("닉네임")
			.gender("중성")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		GoalRetirementCalculateRequest request = GoalRetirementCalculateRequest.builder()
			.startAt(fixedNow)
			.endAt(LocalDateTime.of(2055,1, 31,0,0,0))
			.expectedLifespan(85L)
			.monthlyExpense(2_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		// when
		Long targetAmount = goalRetirementCalculateService.calculateTargetAmount(request, user.getId());

		// then
		assertThat(targetAmount).isEqualTo(502_000_000L); // 예상 사망일: 1990 + 85 = 2075 -> 2055.01.31~2075.12.31 = 20년 * 251개월 * 2백만원 = 5.02억
	}

	@Test
	@DisplayName("유저가 유효하지 않으면 예외를 던진다")
	void calculateTargetAmount_유저가_유효하지_않으면_USER_NOT_FOUND_예외를_던진다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email1@email.com")
			.password("5678")
			.nickname("닉네임1")
			.gender("중성")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.deletedAt(LocalDateTime.now())
			.build();

		userRepository.save(user);

		GoalRetirementCalculateRequest request = GoalRetirementCalculateRequest.builder()
			.startAt(fixedNow)
			.endAt(LocalDateTime.of(2055,1, 31,0,0,0))
			.expectedLifespan(85L)
			.monthlyExpense(2_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalRetirementCalculateService.calculateTargetAmount(request, user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("기대수명 입력이 현재 나이보다 작으면 예외를 던진다")
	void calculateTargetAmount_기대수명_입력이_현재_나이보다_작으면_INVALID_EXPECTED_LIFESPAN_예외를_던진다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email2@email.com")
			.password("5678")
			.nickname("닉네임2")
			.gender("중성")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		GoalRetirementCalculateRequest request = GoalRetirementCalculateRequest.builder()
			.startAt(fixedNow)
			.endAt(LocalDateTime.of(2055,1, 31,0,0,0))
			.expectedLifespan(30L)
			.monthlyExpense(2_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalRetirementCalculateService.calculateTargetAmount(request, user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_EXPECTED_LIFESPAN);
	}

	@Test
	@DisplayName("종료일이 기대수명 이후 시점이면 예외를 던진다")
	void calculateTargetAmount_기대수명_입력이_목표_종료일_이전이면_GOAL_RETIREMENT_LIFESPAN_BEFORE_END_DATE_예외를_던진다() {
		// given
		User user = User.builder()
			.name("이름3")
			.email("email3@email.com")
			.password("5678")
			.nickname("닉네임3")
			.gender("중성")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		GoalRetirementCalculateRequest request = GoalRetirementCalculateRequest.builder()
			.startAt(fixedNow)
			.endAt(LocalDateTime.of(2055,1, 31,0,0,0))
			.expectedLifespan(50L)
			.monthlyExpense(2_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalRetirementCalculateService.calculateTargetAmount(request, user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_RETIREMENT_LIFESPAN_BEFORE_END_DATE);
	}
}
