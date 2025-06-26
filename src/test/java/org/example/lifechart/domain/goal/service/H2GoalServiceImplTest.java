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
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalEtcRequest;
import org.example.lifechart.domain.goal.dto.request.GoalHousingRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequest;
import org.example.lifechart.domain.goal.dto.request.GoalSearchCondition;
import org.example.lifechart.domain.goal.dto.request.GoalUpdateRequest;
import org.example.lifechart.domain.goal.dto.response.CursorPageResponse;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalEtcInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.dto.response.GoalRetirementInfoResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class H2GoalServiceImplTest {

	@Autowired
	private GoalRepository goalRepository;

	@Autowired
	private GoalRetirementRepository goalRetirementRepository;

	@Autowired
	private GoalHousingRepository goalHousingRepository;

	@Autowired
	private GoalEtcRepository goalEtcRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GoalDetailFetcherFactory goalDetailFetcherFactory;

	@Autowired
	private GoalServiceImpl goalService;

	LocalDateTime fixedNow = LocalDateTime.of(2025,9,1,0,0);

	@Test
	@DisplayName("은퇴 목표 생성에 성공한다.")
	void createGoal_은퇴_목표_생성에_성공한다() {
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

		GoalRetirementRequest detail = GoalRetirementRequest.builder()
			.expectedLifespan(90L)
			.monthlyExpense(2_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		GoalCreateRequest request = GoalCreateRequest.builder()
			.title("젊은 한량 되기")
			.category(Category.RETIREMENT)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(5))
			.detail(detail)
			.targetAmount(502_000_000L)
			.share(Share.PRIVATE)
			.tags(List.of("한량"))
			.build();

		// when
		GoalResponse response = goalService.createGoal(request, user.getId());

		// then
		assertThat(response.getGoalId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("주거 목표 생성에 성공한다.")
	void createGoal_주거_목표_생성에_성공한다() {
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

		GoalHousingRequest detail = GoalHousingRequest.builder()
			.region("서울")
			.subregion("서남권")
			.housingType(HousingType.APARTMENT)
			.area(100L)
			.build();

		GoalCreateRequest request = GoalCreateRequest.builder()
			.title("여의도 집 사기")
			.category(Category.HOUSING)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(2))
			.detail(detail)
			.targetAmount(1_432_100_000L)
			.share(Share.PRIVATE)
			.tags(List.of("여의도", "집"))
			.build();

		// when
		GoalResponse response = goalService.createGoal(request, user.getId());

		// then
		assertThat(response.getGoalId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("기타 목표 생성에 성공한다.")
	void createGoal_기타_목표_생성에_성공한다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email2@email.com")
			.password("5678")
			.nickname("닉네임2")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		GoalEtcRequest detail = GoalEtcRequest.builder()
			.theme("여행")
			.expectedPrice(30_000_000L)
			.build();

		GoalCreateRequest request = GoalCreateRequest.builder()
			.title("30일 크루즈 세계일주")
			.category(Category.ETC)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(3))
			.detail(detail)
			.targetAmount(30_000_000L)
			.share(Share.PRIVATE)
			.tags(List.of("크루즈", "세계일주"))
			.build();

		// when
		GoalResponse response = goalService.createGoal(request, user.getId());

		// then
		assertThat(response.getGoalId()).isEqualTo(1L);
	}

	@Test // to do
	@DisplayName("목표의 category와 detail의 입력 양식이 다르면 예외를 던진다.")
	void createGoal_목표의_category와_detail의_양식이_다르면_GOAL_CATEGORY_DETAIL_MISMATCH_예외를_던진다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email3@email.com")
			.password("5678")
			.nickname("닉네임3")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		GoalHousingRequest detail = GoalHousingRequest.builder()
				.region("서울")
				.subregion("서남권")
				.housingType(HousingType.APARTMENT)
				.area(100L)
				.build();

		GoalCreateRequest request = GoalCreateRequest.builder()
				.title("여의도 집 사기")
				.category(Category.RETIREMENT)
				.startAt(fixedNow)
				.endAt(fixedNow.plusYears(2))
				.detail(detail)
				.targetAmount(1_432_100_000L)
				.share(Share.PRIVATE)
				.tags(List.of("여의도"))
				.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
						goalService.createGoal(request, user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_CATEGORY_DETAIL_MISMATCH);
	}

	@Test
	@DisplayName("이미 생성된 은퇴 목표가 있으면 예외를 던진다.")
	void createGoal_이미_생성된_은퇴_목표가_있으면_ONLY_ONE_RETIREMENT_GOAL_예외를_던진다() {
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
			.title("은퇴한 젊은 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("은퇴", "한량"))
			.build();

		goalRepository.save(goal);

		GoalRetirementRequest detail = GoalRetirementRequest.builder()
			.expectedLifespan(90L)
			.monthlyExpense(2_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		GoalCreateRequest request = GoalCreateRequest.builder()
			.title("젊은 한량 되기")
			.category(Category.RETIREMENT)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(5))
			.detail(detail)
			.targetAmount(502_000_000L)
			.share(Share.PRIVATE)
			.tags(List.of("한량"))
			.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.createGoal(request, user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ONLY_ONE_RETIREMENT_GOAL);
	}

	@Test
	@DisplayName("목표 개별 조회에 성공한다")
	void findGoal_목표_개별_조회에_성공한다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email3@email.com")
			.password("5678")
			.nickname("닉네임3")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		Goal goal = Goal.builder()
			.user(user)
			.title("은퇴한 젊은 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("은퇴", "한량"))
			.build();

		goalRepository.save(goal);

		GoalRetirement goalRetirement = GoalRetirement.builder()
			.goal(goal)
			.monthlyExpense(5_000_000L)
			.retirementType(RetirementType.COUPLE)
			.expectedDeathDate(fixedNow.plusYears(50L).toLocalDate())
			.build();

		goalRetirementRepository.save(goalRetirement);

		// when
		GoalInfoResponse goalInfoResponse = goalService.findGoal(goal.getId(), user.getId());
		GoalRetirementInfoResponse retirementInfoResponse = (GoalRetirementInfoResponse) goalInfoResponse.getDetail();

		// then
		assertThat(goalInfoResponse.getCategory()).isEqualTo(Category.RETIREMENT);
		assertThat(goalInfoResponse.getDetail()).isEqualTo(retirementInfoResponse);
		assertThat(retirementInfoResponse.getMonthlyExpense()).isEqualTo(5_000_000L);
		assertThat(retirementInfoResponse.getRetirementType()).isEqualTo(RetirementType.COUPLE);
	}

	@Test
	@DisplayName("존재하지 않는 goalId이거나, 다른 사용자의 목표인 경우 예외를 던진다.")
	void findGoal_존재하지_않는_goalId이거나_다른_사용자의_목표인_경우_GOAL_NOT_FOUND_예외를_던진다() {
		User loginUser = User.builder()
			.name("이름")
			.email("email3@email.com")
			.password("5678")
			.nickname("닉네임3")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		User anotherUser = User.builder()
			.name("다른 유저")
			.email("email00@email.com")
			.password("5678")
			.nickname("다른 유저")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(loginUser);
		userRepository.save(anotherUser);

		Goal goal = Goal.builder()
			.user(anotherUser)
			.title("은퇴한 젊은 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("은퇴", "한량"))
			.build();

		goalRepository.save(goal);

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.findGoal(goal.getId(), loginUser.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_NOT_FOUND);
	}

	@Test
	@DisplayName("목표 삭제에 성공한다.")
	void deleteGoal_목표_삭제에_성공한다() {
		User user = User.builder()
			.name("이름")
			.email("email3@email.com")
			.password("5678")
			.nickname("닉네임3")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		Goal goal = Goal.builder()
			.user(user)
			.title("은퇴한 젊은 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("은퇴", "한량"))
			.build();

		Goal testgoal = Goal.builder()
			.user(user)
			.title("테스트 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("한량"))
			.build();

		goalRepository.save(goal);
		goalRepository.save(testgoal);

		GoalRetirement goalRetirement = GoalRetirement.builder()
			.goal(goal)
			.monthlyExpense(5_000_000L)
			.retirementType(RetirementType.COUPLE)
			.expectedDeathDate(fixedNow.plusYears(50L).toLocalDate())
			.build();

		goalRetirementRepository.save(goalRetirement);

		// when
		goalService.deleteGoal(testgoal.getId(), user.getId());

		// then
		Goal deletedGoal = goalRepository.findById(testgoal.getId()).orElseThrow();
		assertThat(deletedGoal.getStatus()).isEqualTo(Status.DELETED);
	}

	@Test
	@DisplayName("로그인한 유저가 생성한 목표가 아니면 예외를 반환한다.")
	void deleteGoal_로그인한_유저가_생성한_목표가_아니면_GOAL_NOT_FOUND_예외를_던진다() {
		User loginUser = User.builder()
			.name("이름")
			.email("email3@email.com")
			.password("5678")
			.nickname("닉네임3")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		User anotherUser = User.builder()
			.name("다른 유저")
			.email("email00@email.com")
			.password("5678")
			.nickname("다른 유저")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(loginUser);
		userRepository.save(anotherUser);

		Goal goal = Goal.builder()
			.user(anotherUser)
			.title("은퇴한 젊은 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("은퇴", "한량"))
			.build();

		goalRepository.save(goal);

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.deleteGoal(goal.getId(), loginUser.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_NOT_FOUND);
	}

	@Test
	@DisplayName("이미 삭제된 목표인 경우 예외를 던진다.")
	void deleteGoal_이미_삭제된_목표인_경우_GOAL_ALREADY_DELETED_예외를_던진다() {
		User user = User.builder()
			.name("이름")
			.email("email3@email.com")
			.password("5678")
			.nickname("닉네임3")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		Goal goal = Goal.builder()
			.user(user)
			.title("은퇴한 젊은 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("은퇴", "한량"))
			.build();

		Goal testgoal = Goal.builder()
			.user(user)
			.title("테스트 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.DELETED)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("한량"))
			.build();

		goalRepository.save(goal);
		goalRepository.save(testgoal);

		GoalRetirement goalRetirement = GoalRetirement.builder()
			.goal(goal)
			.monthlyExpense(5_000_000L)
			.retirementType(RetirementType.COUPLE)
			.expectedDeathDate(fixedNow.plusYears(50L).toLocalDate())
			.build();

		goalRetirementRepository.save(goalRetirement);

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.deleteGoal(testgoal.getId(), user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_ALREADY_DELETED);
	}

	@Test
	@DisplayName("은퇴 목표가 하나 남았으면 예외를 던진다.")
	void deleteGoal_은퇴_목표가_하나뿐이면_ONLY_ONE_RETIREMENT_GOAL_예외를_던진다() {
		User user = User.builder()
			.name("이름")
			.email("email3@email.com")
			.password("5678")
			.nickname("닉네임3")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(false)
			.build();

		userRepository.save(user);

		Goal testgoal = Goal.builder()
			.user(user)
			.title("테스트 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("한량"))
			.build();

		goalRepository.save(testgoal);

		GoalRetirement goalRetirement = GoalRetirement.builder()
			.goal(testgoal)
			.monthlyExpense(5_000_000L)
			.retirementType(RetirementType.COUPLE)
			.expectedDeathDate(fixedNow.plusYears(50L).toLocalDate())
			.build();

		goalRetirementRepository.save(goalRetirement);

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.deleteGoal(testgoal.getId(), user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ONLY_ONE_RETIREMENT_GOAL);
	}

	@Test // to do
	@DisplayName("주거 목표 수정에 성공한다.")
	void updateGoal_주거_목표_수정에_성공한다() {
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
			.title("여의도 집 사기")
			.category(Category.HOUSING)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("여의도", "집"))
			.build();

		goalRepository.save(goal);

		GoalHousing goalHousing = GoalHousing.builder()
				.goal(goal)
				.region("서울")
				.subregion("서북권")
				.housingType(HousingType.APARTMENT)
				.area(84L)
				.build();

		goalHousingRepository.save(goalHousing);

		GoalHousingRequest detail = GoalHousingRequest.builder()
			.region("서울")
			.subregion("도심")
			.housingType(HousingType.APARTMENT)
			.area(100L)
			.build();

		GoalUpdateRequest request = GoalUpdateRequest.builder()
			.title("여의도 큰 집 사기")
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(2))
			.detail(detail)
			.targetAmount(1_432_100_000L)
			.share(Share.PRIVATE)
			.tags(List.of("여의도", "큰집"))
			.build();

		// when
		GoalResponse response = goalService.updateGoal(request, goal.getId(), user.getId());

		// then
		Goal updatedGoal = goalRepository.findById(response.getGoalId()).orElseThrow();
		GoalHousing updatedGoalHousing = goalHousingRepository.findByGoalId(response.getGoalId()).orElseThrow();
		assertThat(updatedGoal.getTitle()).isEqualTo("여의도 큰 집 사기");
		assertThat(updatedGoal.getTargetAmount()).isEqualTo(1_432_100_000L);
		assertThat(updatedGoalHousing.getArea()).isEqualTo(100L);
		assertThat(updatedGoalHousing.getSubregion()).isEqualTo("도심");
	}

	@Test // to do
	@DisplayName("은퇴 목표 수정에 성공한다.")
	void updateGoal_은퇴_목표_수정에_성공한다() {
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
			.title("테스트 한량 되기")
			.category(Category.RETIREMENT)
			.targetAmount(1_500_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("한량"))
			.build();

		goalRepository.save(goal);

		GoalRetirement goalRetirement = GoalRetirement.builder()
			.goal(goal)
			.monthlyExpense(3_000_000L)
			.retirementType(RetirementType.COUPLE)
			.expectedDeathDate(fixedNow.plusYears(50L).toLocalDate())
			.build();

		goalRetirementRepository.save(goalRetirement);

		GoalRetirementRequest detail = GoalRetirementRequest.builder()
				.expectedLifespan(90L)
				.monthlyExpense(5_000_000L)
				.retirementType(RetirementType.COUPLE)
				.build();

		GoalUpdateRequest request = GoalUpdateRequest.builder()
				.title("젊은 한량 되기")
				.startAt(fixedNow)
				.endAt(fixedNow.plusYears(20L))
				.detail(detail)
				.targetAmount(502_000_000L)
				.share(Share.PRIVATE)
				.build();

		// when
		GoalResponse response = goalService.updateGoal(request, goal.getId(), user.getId());

		// then
		Goal updatedGoal = goalRepository.findById(response.getGoalId()).orElseThrow();
		GoalRetirement updatedGoalRetirement = goalRetirementRepository.findByGoalId(response.getGoalId()).orElseThrow();
		assertThat(updatedGoal.getTitle()).isEqualTo("젊은 한량 되기");
		assertThat(updatedGoalRetirement.getMonthlyExpense()).isEqualTo(5_000_000L);
	}

	@Test // to do
	@DisplayName("기타 목표 수정에 성공한다.")
	void updateGoal_기타_목표_수정에_성공한다() {
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
			.title("여행을 떠나요")
			.category(Category.ETC)
			.targetAmount(5_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("여행"))
			.build();

		goalRepository.save(goal);

		GoalEtc goalEtc = GoalEtc.builder()
			.goal(goal)
			.theme("여행")
			.expectedPrice(5_000_000L)
			.build();

		goalEtcRepository.save(goalEtc);

		GoalEtcRequest detail = GoalEtcRequest.builder()
			.theme("세계 여행")
			.expectedPrice(30_000_000L)
			.build();

		GoalUpdateRequest request = GoalUpdateRequest.builder()
			.title("30일 크루즈 세계일주")
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(4))
			.detail(detail)
			.targetAmount(30_000_000L)
			.share(Share.PRIVATE)
			.tags(List.of("크루즈", "세계일주"))
			.build();

		// when
		GoalResponse response = goalService.updateGoal(request, goal.getId(), user.getId());

		// then
		Goal updatedGoal = goalRepository.findById(response.getGoalId()).orElseThrow();
		GoalEtc updatedGoalEtc = goalEtcRepository.findByGoalId(response.getGoalId()).orElseThrow();
		assertThat(updatedGoal.getTitle()).isEqualTo("30일 크루즈 세계일주");
		assertThat(updatedGoalEtc.getTheme()).isEqualTo("세계 여행");
		assertThat(updatedGoalEtc.getExpectedPrice()).isEqualTo(30_000_000L);


	}

	@Test // to do
	@DisplayName("조회한 목표의 category와 수정할 detail 양식이 다르면 예외를 던진다.")
	void updateGoal_조회한_목표의_category와_수정할_detail양식이_다르면_GOAL_CATEGORY_DETAIL_MISMATCH_예외를_던진다() {
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
			.title("여행을 떠나요")
			.category(Category.ETC)
			.targetAmount(5_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("여행"))
			.build();

		goalRepository.save(goal);

		GoalEtc goalEtc = GoalEtc.builder()
			.goal(goal)
			.theme("여행")
			.expectedPrice(5_000_000L)
			.build();

		goalEtcRepository.save(goalEtc);

		GoalRetirementRequest detail = GoalRetirementRequest.builder()
			.expectedLifespan(90L)
			.monthlyExpense(5_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		GoalUpdateRequest request = GoalUpdateRequest.builder()
			.title("젊은 한량 되기")
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(20L))
			.detail(detail)
			.targetAmount(502_000_000L)
			.share(Share.PRIVATE)
			.build();

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
				goalService.updateGoal(request, goal.getId(), user.getId()));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_CATEGORY_DETAIL_MISMATCH);
	}

	@Test
	@DisplayName("내 목표 전체 조회에 성공한다.")
	void findMyGoals_내_목표_전체_조회에_성공한다() {
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
			.title("여행을 떠나요")
			.category(Category.ETC)
			.targetAmount(5_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("여행"))
			.build();

		Goal secondGoal = Goal.builder()
			.user(user)
			.title("세계 여행")
			.category(Category.ETC)
			.targetAmount(10_000_000L)
			.startAt(fixedNow)
			.endAt(fixedNow.plusYears(10))
			.status(Status.ACTIVE)
			.share(Share.PRIVATE)
			.commentCount(0)
			.likeCount(0)
			.tags(List.of("여행"))
			.build();

		goalRepository.saveAll(List.of(goal, secondGoal));

		GoalSearchCondition condition = new GoalSearchCondition(null, 10, Status.ACTIVE, Category.ETC, Share.PRIVATE);

		// when
		CursorPageResponse<GoalSummaryResponse> response = goalService.findMyGoals(user.getId(), condition);

		// then
		System.out.println(goalRepository.findAll());
		System.out.println("User ID: " + user.getId());
		assertThat(response.contents().size()).isEqualTo(2);
		assertThat(response.nextCursor()).isNull();
		assertThat(response.hasNext()).isFalse();
	}

	@Test
	@DisplayName("유효한 유저가 아닌 경우 예외를 던진다.")
	void findMyGoals_유효한_유저가_아닌_경우_USER_NOT_FOUND_예외를_던진다() {
		// given
		User user = User.builder()
			.name("이름")
			.email("email1@email.com")
			.password("5678")
			.nickname("닉네임1")
			.gender("male")
			.birthDate(LocalDate.of(1990,1,1))
			.isDeleted(true)
			.deletedAt(fixedNow)
			.build();

		userRepository.save(user);

		GoalSearchCondition condition = new GoalSearchCondition(null, 10, Status.ACTIVE, Category.ETC, Share.PRIVATE);

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			goalService.findMyGoals(user.getId(), condition));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}
}