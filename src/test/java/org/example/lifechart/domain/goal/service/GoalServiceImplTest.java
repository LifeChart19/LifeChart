package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalEtcRequest;
import org.example.lifechart.domain.goal.dto.request.GoalHousingRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequest;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalEtcInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.fetcher.GoalDetailFetcherFactory;
import org.example.lifechart.domain.goal.repository.GoalEtcRepository;
import org.example.lifechart.domain.goal.repository.GoalHousingRepository;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.goal.repository.GoalRetirementRepository;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

	@Mock
	private GoalRepository goalRepository;

	@Mock
	private GoalRetirementRepository goalRetirementRepository;

	@Mock
	private GoalHousingRepository goalHousingRepository;

	@Mock
	private GoalEtcRepository goalEtcRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private GoalDetailFetcherFactory goalDetailFetcherFactory;

	@InjectMocks
	private GoalServiceImpl goalService;

	@Test
	@DisplayName("은퇴 목표 생성에 성공한다.")
	void 은퇴_목표_생성에_성공한다() {
		// given
		User user = User.builder()
			.id(1L)
			.gender("male")
			.birthDate(LocalDate.of(1990,01,01))
			.isDeleted(false)
			.build();

		GoalRetirementRequest detail = GoalRetirementRequest.builder()
			.expectedLifespan(90L)
			.monthlyExpense(2_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		LocalDateTime currentTime = LocalDateTime.now();

		GoalCreateRequest request = GoalCreateRequest.builder()
			.title("젊은 한량 되기")
			.category(Category.RETIREMENT)
			.startAt(currentTime)
			.endAt(currentTime.plusMonths(6))
			.detail(detail)
			.targetAmount(502_000_000L)
			.share(Share.PRIVATE)
			.build();

		Goal goal = request.toEntity(user);
		goal = goal.toBuilder().id(1L).build();
		GoalRetirement goalRetirement = detail.toEntity(goal, user.getBirthDate().getYear());
		goalRetirement = goalRetirement.toBuilder().id(1L).build();

		given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));
		given(goalRepository.save(any())).willReturn(goal);
		given(goalRetirementRepository.save(any())).willReturn(goalRetirement);

		// when
		GoalResponse response = goalService.createGoal(request, user.getId());

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(user.getId());
		verify(goalRepository).save(any(Goal.class));
		verify(goalRetirementRepository).save(any(GoalRetirement.class));
		assertThat(goal.getTitle()).isEqualTo("젊은 한량 되기");
		assertThat(goalRetirement.getGoal()).isEqualTo(goal);
		assertThat(response.getGoalId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("주거 목표 생성에 성공한다.")
	void 주거_목표_생성에_성공한다() {
		// given
		User user = User.builder()
			.id(1L)
			.gender("male")
			.birthDate(LocalDate.of(1990,01,01))
			.isDeleted(false)
			.build();

		GoalHousingRequest detail = GoalHousingRequest.builder()
			.region("서울")
			.subregion("서남권")
			.housingType(HousingType.APARTMENT)
			.area(100L)
			.build();

		LocalDateTime currentTime = LocalDateTime.now();

		GoalCreateRequest request = GoalCreateRequest.builder()
			.title("여의도 집 사기")
			.category(Category.RETIREMENT)
			.startAt(currentTime)
			.endAt(currentTime.plusMonths(6))
			.detail(detail)
			.targetAmount(1_432_100_000L)
			.share(Share.PRIVATE)
			.build();

		Goal goal = request.toEntity(user);
		goal = goal.toBuilder().id(1L).build();
		GoalHousing goalHousing = detail.toEntity(goal);
		goalHousing = goalHousing.toBuilder().id(1L).build();

		given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));
		given(goalRepository.save(any())).willReturn(goal);
		given(goalHousingRepository.save(any())).willReturn(goalHousing);

		// when
		GoalResponse response = goalService.createGoal(request, user.getId());

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(user.getId());
		verify(goalRepository).save(any(Goal.class));
		verify(goalHousingRepository).save(any(GoalHousing.class));
		assertThat(goal.getTitle()).isEqualTo("여의도 집 사기");
		assertThat(goalHousing.getGoal()).isEqualTo(goal);
		assertThat(response.getGoalId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("기타 목표 생성에 성공한다.")
	void 기타_목표_생성에_성공한다() {
		// given
		User user = User.builder()
			.id(1L)
			.gender("male")
			.birthDate(LocalDate.of(1990,01,01))
			.isDeleted(false)
			.build();

		GoalEtcRequest detail = GoalEtcRequest.builder()
			.theme("여행")
			.expectedPrice(30_000_000L)
			.build();

		LocalDateTime currentTime = LocalDateTime.now();

		GoalCreateRequest request = GoalCreateRequest.builder()
			.title("30일 크루즈 세계일주")
			.category(Category.ETC)
			.startAt(currentTime)
			.endAt(currentTime.plusYears(4))
			.detail(detail)
			.targetAmount(30_000_000L)
			.share(Share.PRIVATE)
			.build();

		Goal goal = request.toEntity(user);
		goal = goal.toBuilder().id(1L).build();
		GoalEtc goalEtc = detail.toEntity(goal);
		goalEtc = goalEtc.toBuilder().id(1L).build();

		given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));
		given(goalRepository.save(any())).willReturn(goal);
		given(goalEtcRepository.save(any())).willReturn(goalEtc);

		// when
		GoalResponse response = goalService.createGoal(request, user.getId());

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(user.getId());
		verify(goalRepository).save(any(Goal.class));
		verify(goalEtcRepository).save(any(GoalEtc.class));
		assertThat(goal.getTitle()).isEqualTo("30일 크루즈 세계일주");
		assertThat(goalEtc.getTheme()).isEqualTo("여행");
		assertThat(goalEtc.getGoal()).isEqualTo(goal);
		assertThat(response.getGoalId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("목표 개별 조회에 성공한다")
	void findGoal_목표_개별_조회에_성공한다() {
		// given
		Goal goal = Goal.builder()
			.id(1L)
			.category(Category.ETC)
			.build();

		User user = User.builder()
			.id(1L)
			.build();

		GoalDetailInfoResponse goalDetailInfoResponse = GoalEtcInfoResponse.builder()
			.expectedPrice(1_000_000L)
			.theme("여행")
			.build();

		given(goalRepository.findByIdAndUserId(goal.getId(), user.getId())).willReturn(Optional.of(goal));
		given(goalDetailFetcherFactory.getDetail(goal)).willReturn(goalDetailInfoResponse);

		// when
		GoalInfoResponse goalInfoResponse = goalService.findGoal(goal.getId(), user.getId());
		GoalEtcInfoResponse etcInfoResponse = (GoalEtcInfoResponse) goalInfoResponse.getDetail();

		// then
		verify(goalRepository).findByIdAndUserId(goal.getId(), user.getId());
		assertThat(goalInfoResponse.getCategory()).isEqualTo(Category.ETC);
		assertThat(goalInfoResponse.getDetail()).isEqualTo(goalDetailInfoResponse);
		assertThat(etcInfoResponse.getTheme()).isEqualTo("여행");
		assertThat(etcInfoResponse.getExpectedPrice()).isEqualTo(1_000_000L);
	}

	@Test
	@DisplayName("존재하지 않는 goalId이거나, 다른 사용자의 목표인 경우 예외를 던진다.")
	void findGoal_존재하지_않는_goalId이거나_다른_사용자의_목표인_경우_GOAL_NOT_FOUND_예외를_던진다() {
		// given
		User loginUser = User.builder()
			.id(1L)
			.build();

		User anotherUser = User.builder()
			.id(2L)
			.build();

		Goal goal = Goal.builder()
			.id(1L)
			.user(anotherUser)
			.build();

		given(goalRepository.findByIdAndUserId(goal.getId(), loginUser.getId())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.findGoal(goal.getId(), loginUser.getId()));

		// then
		verify(goalRepository).findByIdAndUserId(goal.getId(), loginUser.getId());
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_NOT_FOUND);
	}
}