package org.example.lifechart.domain.shareGoal.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.follow.entity.Follow;
import org.example.lifechart.domain.follow.repository.FollowRepository;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class ShareGoalServiceImplTest {

	@InjectMocks
	private ShareGoalServiceImpl shareGoalService;

	@Mock
	private GoalRepository goalRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FollowRepository followRepository;
	User authUser;
	User user1;
	User user2;
	User user3;
	Goal authUserGoalAll;
	Goal authUserGoalFollower;
	Goal user1GoalAll;
	Goal user1GoalFollower;
	Goal user2GoalAll;
	Goal user2GoalFollower;
	Goal user3GoalAll;
	Goal user3GoalFollower;
	Follow follow1;
	Follow follow2;

	@BeforeEach
	void setUp() {
		authUser = User.builder().id(1L).build();
		user1 = User.builder().id(2L).build();
		user2 = User.builder().id(3L).build();
		user3 = User.builder().id(4L).build();
		follow1 = Follow.builder().id(1L).requester(authUser).receiver(user1).build();
		follow2 = Follow.builder().id(2L).requester(authUser).receiver(user2).build();
		authUserGoalAll = Goal.builder()
			.id(1L)
			.user(authUser)
			.share(Share.ALL)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.HOUSING)
			.build();

		authUserGoalFollower = Goal.builder()
			.id(2L)
			.user(authUser)
			.share(Share.FOLLOWER)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.RETIREMENT)
			.build();

		user1GoalAll = Goal.builder()
			.id(3L)
			.user(user1)
			.share(Share.ALL)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.HOUSING)
			.build();

		user1GoalFollower = Goal.builder()
			.id(4L)
			.user(user1)
			.share(Share.FOLLOWER)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.RETIREMENT)
			.build();

		user2GoalAll = Goal.builder()
			.id(5L)
			.user(user2)
			.share(Share.ALL)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.HOUSING)
			.build();

		user2GoalFollower = Goal.builder()
			.id(6L)
			.user(user2)
			.share(Share.FOLLOWER)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.RETIREMENT)
			.build();

		user3GoalAll = Goal.builder()
			.id(7L)
			.user(user3)
			.share(Share.ALL)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.HOUSING)
			.build();

		user3GoalFollower = Goal.builder()
			.id(8L)
			.user(user3)
			.share(Share.FOLLOWER)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.RETIREMENT)
			.build();
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - default")
	void getShareGoals_Ok() {
		// given
		List<Goal> goalList = List.of(user3GoalAll, user2GoalFollower, user2GoalAll,
			user1GoalFollower, user1GoalAll, authUserGoalAll);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), null, 10, null, null)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 10, null, null
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(1, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - size 2일 때 nextCursor가 제대로 반환되는지")
	void getShareGoals_Ok1() {
		// given
		List<Goal> goalList = List.of(user3GoalAll, user2GoalFollower);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), null, 2, null, null)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 2, null, null
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(6, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - cursorId 값을 주고 제대로 동작하는지")
	void getShareGoals_Ok2() {
		// given
		List<Goal> goalList = List.of(user2GoalAll, user1GoalFollower);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), 6L, 2, null, null)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), 6L, 2, null, null
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(4, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - category 값을 넣었을 때 제대로 작동하는지")
	void getShareGoals_Ok3() {
		// given
		List<Goal> goalList = List.of(user2GoalFollower, user1GoalFollower);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), null, 10, Category.RETIREMENT, null)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 10, Category.RETIREMENT, null
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(4, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - share 값이 ALL인 경우 제대로 작동하는지")
	void getShareGoals_Ok4() {
		// given
		List<Goal> goalList = List.of(user3GoalAll, user2GoalAll, user1GoalAll, authUserGoalAll);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), null, 10, null, Share.ALL)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 10, null, Share.ALL
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(1, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - share 값이 FOLLOWER인 경우 제대로 작동하는지")
	void getShareGoals_Ok5() {
		// given
		List<Goal> goalList = List.of(user2GoalFollower, user1GoalFollower);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), null, 10, null, Share.FOLLOWER)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 10, null, Share.FOLLOWER
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(4, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - 모든 값을 줬을 때 제대로 작동하는지")
	void getShareGoals_Ok6() {
		// given
		List<Goal> goalList = List.of(user2GoalFollower, user1GoalFollower);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), 8L, 6, Category.RETIREMENT, Share.FOLLOWER)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), 8L, 6, Category.RETIREMENT, Share.FOLLOWER
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(4, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 실패")
	void getShareGoals_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.empty());
		//when then
		CustomException exception = assertThrows(CustomException.class, () -> shareGoalService.getShareGoals(
			authUser.getId(), null, 10, null, null)
		);
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("특정 유저 공유 목표 조회 성공 - 팔로우 한 대상")
	void getShareGoalsToUser_Ok() {
		// given
		List<Goal> goalList = List.of(user1GoalFollower, user1GoalAll);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList.stream().map(ShareGoalResponseDto::from).toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(user1.getId())).willReturn(Optional.of(user1));
		given(goalRepository.findByAuthIdAndUserId(authUser.getId(), user1.getId())).willReturn(goalList);
		//when
		List<ShareGoalResponseDto> result = shareGoalService.getShareGoalsToUser(authUser.getId(),
			user1.getId());

		//then
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
	}

	@Test
	@DisplayName("특정 유저 공유 목표 조회 성공 - 팔로우 안 한 대상")
	void getShareGoalsToUser_Ok1() {
		// given
		List<Goal> goalList = List.of(user3GoalAll);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList.stream().map(ShareGoalResponseDto::from).toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(user3.getId())).willReturn(Optional.of(user3));
		given(goalRepository.findByAuthIdAndUserId(authUser.getId(), user3.getId())).willReturn(goalList);
		//when
		List<ShareGoalResponseDto> result = shareGoalService.getShareGoalsToUser(authUser.getId(),
			user3.getId());

		//then
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
	}

	@Test
	@DisplayName("특정 유저 공유 목표 조회 실패 - 로그인 유저 x")
	void getShareGoalsToUser_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.empty());
		// when, then
		CustomException exception = assertThrows(CustomException.class,
			() -> shareGoalService.getShareGoalsToUser(authUser.getId(),
				user1.getId()));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("특정 유저 공유 목표 조회 실패 - 대상 유저 x")
	void getShareGoalsToUser_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(user1.getId())).willReturn(Optional.empty());
		// when, then
		CustomException exception = assertThrows(CustomException.class,
			() -> shareGoalService.getShareGoalsToUser(authUser.getId(),
				user1.getId()));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}
}