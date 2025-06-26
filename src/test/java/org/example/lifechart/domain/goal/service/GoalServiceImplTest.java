package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.request.*;
import org.example.lifechart.domain.goal.dto.response.CursorPageResponse;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalEtcInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.dto.response.GoalSummaryResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
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
	void createGoal_은퇴_목표_생성에_성공한다() {
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

		Goal goal = Goal.from(request, user);
		goal = goal.toBuilder().id(1L).build();
		GoalRetirement goalRetirement = GoalRetirement.from(goal, detail, user.getBirthDate().getYear());
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
	void createGoal_주거_목표_생성에_성공한다() {
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
			.category(Category.HOUSING)
			.startAt(currentTime)
			.endAt(currentTime.plusMonths(6))
			.detail(detail)
			.targetAmount(1_432_100_000L)
			.share(Share.PRIVATE)
			.build();

		Goal goal = Goal.from(request, user);
		goal = goal.toBuilder().id(1L).build();
		GoalHousing goalHousing = GoalHousing.from(goal, detail);
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
	void createGoal_기타_목표_생성에_성공한다() {
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

		Goal goal = Goal.from(request, user);
		goal = goal.toBuilder().id(1L).build();
		GoalEtc goalEtc = GoalEtc.from(goal, detail);
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

	@Test // to do
	@DisplayName("목표의 category와 detail의 입력 양식이 다르면 예외를 던진다.")
	void createGoal_목표의_category와_detail의_양식이_다르면_GOAL_CATEGORY_DETAIL_MISMATCH_예외를_던진다() {
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

		given(userRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(user));

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
						goalService.createGoal(request, user.getId()));

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(user.getId());
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_CATEGORY_DETAIL_MISMATCH);
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

	@Test
	@DisplayName("목표 삭제에 성공한다.")
	void deleteGoal_목표_삭제에_성공한다() {
		// given
		User user = User.builder()
			.id(1L)
			.build();
		Goal goal = Goal.builder()
			.id(1L)
			.status(Status.ACTIVE)
			.build();

		given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));
		given(goalRepository.findByIdAndUserId(goal.getId(), user.getId())).willReturn(Optional.of(goal));

		// when
		goalService.deleteGoal(goal.getId(), user.getId());

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(user.getId());
		verify(goalRepository).findByIdAndUserId(goal.getId(), user.getId());
		assertThat(goal.getStatus()).isEqualTo(Status.DELETED);
	}

	@Test
	@DisplayName("로그인한 유저가 생성한 목표가 아니면 예외를 반환한다.")
	void deleteGoal_로그인한_유저가_생성한_목표가_아니면_GOAL_NOT_FOUND_예외를_던진다() {
		// given
		User user = User.builder()
			.id(1L)
			.build();

		User anotherUser = User.builder()
			.id(2L)
			.build();

		Goal goal = Goal.builder()
			.id(1L)
			.user(anotherUser)
			.build();

		given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));
		given(goalRepository.findByIdAndUserId(goal.getId(), user.getId())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.deleteGoal(goal.getId(), user.getId()));

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(user.getId());
		verify(goalRepository).findByIdAndUserId(goal.getId(), user.getId());
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_NOT_FOUND);
	}

	@Test
	@DisplayName("이미 삭제된 목표인 경우 예외를 던진다.")
	void deleteGoal_이미_삭제된_목표인_경우_GOAL_ALREADY_DELETED_예외를_던진다() {
		// given
		User user = User.builder()
			.id(1L)
			.build();
		Goal goal = Goal.builder()
			.id(1L)
			.user(user)
			.status(Status.DELETED)
			.build();

		given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));
		given(goalRepository.findByIdAndUserId(goal.getId(), user.getId())).willReturn(Optional.of(goal));

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.deleteGoal(goal.getId(), user.getId()));

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(user.getId());
		verify(goalRepository).findByIdAndUserId(goal.getId(), user.getId());
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_ALREADY_DELETED);
	}

	@Test // to do
	@DisplayName("주거 목표 수정에 성공한다.")
	void updateGoal_주거_목표_수정에_성공한다() {
		// given
		User user = User.builder()
				.id(1L)
				.build();

		Goal goal = Goal.builder()
				.id(1L)
				.user(user)
				.title("목표명")
				.category(Category.HOUSING)
				.targetAmount(1000L)
				.startAt(LocalDateTime.now())
				.endAt(LocalDateTime.now().plusMonths(6))
				.status(Status.ACTIVE)
				.share(Share.ALL)
				.build();

		GoalHousing goalHousing = GoalHousing.builder()
				.id(1L)
				.goal(goal)
				.region("서울")
				.subregion("서북권")
				.housingType(HousingType.APARTMENT)
				.area(84L)
				.build();

		GoalHousingRequest detail = GoalHousingRequest.builder()
				.region("서울")
				.subregion("서남권")
				.housingType(HousingType.APARTMENT)
				.area(100L)
				.build();

		LocalDateTime currentTime = LocalDateTime.now();

		GoalUpdateRequest request = GoalUpdateRequest.builder()
				.title("여의도 집 사기")
				.startAt(currentTime)
				.endAt(currentTime.plusMonths(6))
				.detail(detail)
				.targetAmount(1_432_100_000L)
				.share(Share.PRIVATE)
				.build();

		given(userRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(user));
		given(goalRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(goal));
		given(goalHousingRepository.findByGoalId(1L)).willReturn(Optional.of(goalHousing));

		// when
		GoalResponse response = goalService.updateGoal(request, goal.getId(), user.getId());

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(1L);
		verify(goalRepository).findByIdAndUserId(1L, 1L);
		verify(goalHousingRepository).findByGoalId(1L);
		assertThat(response.getGoalId()).isEqualTo(1L);
		assertThat(goal.getTitle()).isEqualTo("여의도 집 사기");
		assertThat(goalHousing.getArea()).isEqualTo(100L);
	}

	@Test // to do
	@DisplayName("은퇴 목표 수정에 성공한다.")
	void updateGoal_은퇴_목표_수정에_성공한다() {
		// given
		User user = User.builder()
				.id(1L)
				.gender("male")
				.birthDate(LocalDate.of(1990,01,01))
				.isDeleted(false)
				.build();

		Goal goal = Goal.builder()
				.id(1L)
				.user(user)
				.title("목표명")
				.category(Category.RETIREMENT)
				.targetAmount(1_000_000_000L)
				.startAt(LocalDateTime.now())
				.endAt(LocalDateTime.now().plusMonths(6))
				.status(Status.ACTIVE)
				.share(Share.ALL)
				.build();

		GoalRetirement goalRetirement = GoalRetirement.builder()
				.id(1L)
				.goal(goal)
				.expectedDeathDate(LocalDate.now().plusYears(50))
				.monthlyExpense(5_000_000L)
				.build();

		GoalRetirementRequest detail = GoalRetirementRequest.builder()
				.expectedLifespan(90L)
				.monthlyExpense(3_000_000L)
				.retirementType(RetirementType.COUPLE)
				.build();

		LocalDateTime currentTime = LocalDateTime.now();

		GoalUpdateRequest request = GoalUpdateRequest.builder()
				.title("젊은 한량 되기")
				.startAt(currentTime)
				.endAt(currentTime.plusMonths(6))
				.detail(detail)
				.targetAmount(502_000_000L)
				.share(Share.PRIVATE)
				.build();

		given(userRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(user));
		given(goalRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(goal));
		given(goalRetirementRepository.findByGoalId(1L)).willReturn(Optional.of(goalRetirement));

		// when
		GoalResponse response = goalService.updateGoal(request, goal.getId(), user.getId());

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(1L);
		verify(goalRepository).findByIdAndUserId(1L, 1L);
		verify(goalRetirementRepository).findByGoalId(1L);
		assertThat(response.getGoalId()).isEqualTo(1L);
		assertThat(goal.getTitle()).isEqualTo("젊은 한량 되기");
		assertThat(goalRetirement.getMonthlyExpense()).isEqualTo(3_000_000L);
	}

	@Test // to do
	@DisplayName("기타 목표 수정에 성공한다.")
	void updateGoal_기타_목표_수정에_성공한다() {
		// given
		User user = User.builder()
				.id(1L)
				.gender("male")
				.birthDate(LocalDate.of(1990,01,01))
				.isDeleted(false)
				.build();

		Goal goal = Goal.builder()
				.id(1L)
				.user(user)
				.title("목표명")
				.category(Category.ETC)
				.targetAmount(1_000_000_000L)
				.startAt(LocalDateTime.now())
				.endAt(LocalDateTime.now().plusMonths(6))
				.status(Status.ACTIVE)
				.share(Share.ALL)
				.build();

		GoalEtc goalEtc = GoalEtc.builder()
				.id(1L)
				.goal(goal)
				.theme("여행")
				.expectedPrice(5_000_000L)
				.build();


		GoalEtcRequest detail = GoalEtcRequest.builder()
				.theme("여행")
				.expectedPrice(30_000_000L)
				.build();

		LocalDateTime currentTime = LocalDateTime.now();

		GoalUpdateRequest request = GoalUpdateRequest.builder()
				.title("30일 크루즈 세계일주")
				.startAt(currentTime)
				.endAt(currentTime.plusYears(4))
				.detail(detail)
				.targetAmount(30_000_000L)
				.share(Share.PRIVATE)
				.build();

		given(userRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(user));
		given(goalRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(goal));
		given(goalEtcRepository.findByGoalId(1L)).willReturn(Optional.of(goalEtc));

		// when
		GoalResponse response = goalService.updateGoal(request, goal.getId(), user.getId());

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(1L);
		verify(goalRepository).findByIdAndUserId(1L, 1L);
		verify(goalEtcRepository).findByGoalId(1L);
		assertThat(response.getGoalId()).isEqualTo(1L);
		assertThat(goal.getTitle()).isEqualTo("30일 크루즈 세계일주");
		assertThat(goalEtc.getTheme()).isEqualTo("여행");

	}

	@Test // to do
	@DisplayName("조회한 목표의 category와 detail이 다르면 예외를 던진다.")
	void updateGoal_조회한_목표의_category와_detail이_다르면_GOAL_CATEGORY_DETAIL_MISMATCH_예외를_던진다() {
		// given
		User user = User.builder()
				.id(1L)
				.build();

		Goal goal = Goal.builder()
				.id(1L)
				.user(user)
				.title("목표명")
				.category(Category.HOUSING)
				.build();

		GoalHousing goalHousing = GoalHousing.builder()
				.id(1L)
				.goal(goal)
				.build();

		GoalEtcRequest detail = GoalEtcRequest.builder()
				.theme("여행")
				.expectedPrice(30_000_000L)
				.build();

		LocalDateTime currentTime = LocalDateTime.now();

		GoalUpdateRequest request = GoalUpdateRequest.builder()
				.title("30일 크루즈 세계일주")
				.startAt(currentTime)
				.endAt(currentTime.plusYears(4))
				.detail(detail)
				.targetAmount(30_000_000L)
				.share(Share.PRIVATE)
				.build();

		given(userRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(user));
		given(goalRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(goal));

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
				goalService.updateGoal(request, 1L, 1L));

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(1L);
		verify(goalRepository).findByIdAndUserId(1L, 1L);
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_CATEGORY_DETAIL_MISMATCH);
	}

	@Test
	@DisplayName("내 목표 전체 조회에 성공한다.")
	void findMyGoals_내_목표_전체_조회에_성공한다() {
		// given
		User user = User.builder()
			.id(1L)
			.build();

		Goal firstGoal = Goal.builder()
			.id(1L)
			.user(user)
			.targetAmount(1L)
			.status(Status.ACTIVE)
			.build();

		Goal secondGoal = Goal.builder()
			.id(2L)
			.user(user)
			.targetAmount(2L)
			.status(Status.ACTIVE)
			.build();

		List<Goal> myGoals = List.of(firstGoal, secondGoal);

		GoalSearchCondition condition = new GoalSearchCondition(1L, 10, Status.ACTIVE, null, null);

		given(userRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(user));
		given(goalRepository.searchGoalsWithCursor(1L, condition)).willReturn(myGoals);

		// when
		CursorPageResponse<GoalSummaryResponse> response = goalService.findMyGoals(1L, condition);

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(1L);
		verify(goalRepository).searchGoalsWithCursor(1L, condition);
		assertThat(response.contents().size()).isEqualTo(2);
		assertThat(response.contents().get(0).getTargetAmount()).isEqualTo(1L);
		assertThat(response.nextCursor()).isNull();
		assertThat(response.hasNext()).isFalse();
	}

	@Test
	@DisplayName("유효한 유저가 아닌 경우 예외를 던진다.")
	void findMyGoals_유효한_유저가_아닌_경우_USER_NOT_FOUND_예외를_던진다() {
		// given
		User loginUser = User.builder()
			.id(1L)
			.deletedAt(LocalDateTime.now())
			.build();

		Goal goal = Goal.builder()
			.id(1L)
			.user(loginUser)
			.build();

		GoalSearchCondition condition = new GoalSearchCondition(1L, 10, Status.ACTIVE, null, null);

		given(userRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.findMyGoals(1L, condition));

		// then
		verify(userRepository).findByIdAndDeletedAtIsNull(1L);
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}
}