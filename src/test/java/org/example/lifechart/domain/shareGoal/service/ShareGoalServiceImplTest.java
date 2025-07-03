package org.example.lifechart.domain.shareGoal.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.follow.entity.Follow;
import org.example.lifechart.domain.follow.repository.FollowRepository;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.shareGoal.dto.reqeust.ShareGoalSearchRequestDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalSearchResponseDto;
import org.example.lifechart.domain.shareGoal.enums.Sort;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

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

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ZSetOperations<String, String> zSetOperations;

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
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		authUser = User.builder().id(1L).build();
		user1 = User.builder().id(2L).build();
		user2 = User.builder().id(3L).build();
		user3 = User.builder().id(4L).build();
		follow1 = Follow.builder().id(1L).requester(authUser).receiver(user1).build();
		follow2 = Follow.builder().id(2L).requester(authUser).receiver(user2).build();
		authUserGoalAll = Goal.builder()
			.id(1L)
			.user(authUser)
			.title("집 사고 싶다")
			.share(Share.ALL)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.HOUSING)
			.tags(List.of("집"))
			.commentCount(5)
			.likeCount(5)
			.build();
		Field createdAt = BaseEntity.class.getDeclaredField("createdAt");
		createdAt.setAccessible(true);
		createdAt.set(authUserGoalAll, LocalDateTime.now().minus(Period.ofDays(6)));

		authUserGoalFollower = Goal.builder()
			.id(2L)
			.user(authUser)
			.title("은퇴 하고 싶다")
			.share(Share.FOLLOWER)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.RETIREMENT)
			.tags(List.of("은퇴"))
			.commentCount(5)
			.likeCount(5)
			.build();

		user1GoalAll = Goal.builder()
			.id(3L)
			.user(user1)
			.title("집 사고 싶다1")
			.share(Share.ALL)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.HOUSING)
			.tags(List.of("집"))
			.commentCount(5)
			.likeCount(5)
			.build();
		createdAt.set(user1GoalAll, LocalDateTime.now().minus(Period.ofDays(6)));

		user1GoalFollower = Goal.builder()
			.id(4L)
			.user(user1)
			.title("은퇴 하고 싶다1")
			.share(Share.FOLLOWER)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.RETIREMENT)
			.tags(List.of("은퇴"))
			.commentCount(5)
			.likeCount(5)
			.build();
		createdAt.setAccessible(true);
		createdAt.set(user1GoalFollower, LocalDateTime.now().minus(Period.ofDays(3)));

		user2GoalAll = Goal.builder()
			.id(5L)
			.user(user2)
			.title("집 사고 싶다2")
			.share(Share.ALL)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.HOUSING)
			.tags(List.of("집"))
			.commentCount(5)
			.likeCount(5)
			.build();
		createdAt.set(user2GoalAll, LocalDateTime.now().minus(Period.ofDays(6)));

		user2GoalFollower = Goal.builder()
			.id(6L)
			.user(user2)
			.title("은퇴 하고 싶다2")
			.share(Share.FOLLOWER)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.RETIREMENT)
			.tags(List.of("은퇴"))
			.commentCount(5)
			.likeCount(5)
			.build();
		createdAt.setAccessible(true);
		createdAt.set(user2GoalFollower, LocalDateTime.now().minus(Period.ofDays(3)));

		user3GoalAll = Goal.builder()
			.id(7L)
			.user(user3)
			.title("집 사고 싶다3")
			.share(Share.ALL)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.HOUSING)
			.tags(List.of("집"))
			.commentCount(2)
			.likeCount(5)
			.build();
		createdAt.set(user2GoalAll, LocalDateTime.now().minus(Period.ofDays(10)));

		user3GoalFollower = Goal.builder()
			.id(8L)
			.user(user3)
			.title("은퇴 하고 싶다3")
			.share(Share.FOLLOWER)
			.status(Status.ACTIVE)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusDays(7))
			.category(Category.RETIREMENT)
			.tags(List.of("은퇴"))
			.commentCount(1)
			.likeCount(1)
			.build();
		createdAt.setAccessible(true);
		createdAt.set(user3GoalFollower, LocalDateTime.now().minus(Period.ofDays(3)));
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
			authUser.getId(), null, null, 10, null, null, Sort.RECENT, null))
			.willReturn(goalList);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 10, null, null, Sort.RECENT, null
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
			authUser.getId(), null, null, 2, null, null, Sort.RECENT, null))
			.willReturn(goalList);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 2, null, null, Sort.RECENT, null
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
		given(goalRepository.findByIdAndStatus(6L, Status.ACTIVE)).willReturn(Optional.of(user2GoalFollower));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), user2GoalFollower, 6L, 2, null, null, Sort.RECENT, null))
			.willReturn(goalList);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), 6L, 2, null, null, Sort.RECENT, null
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
			authUser.getId(), null, null, 10, Category.RETIREMENT, null, Sort.RECENT, null)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 10, Category.RETIREMENT, null, Sort.RECENT, null
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
			authUser.getId(), null, null, 10, null, Share.ALL, Sort.RECENT, null)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 10, null, Share.ALL, Sort.RECENT, null
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
			authUser.getId(), null, null, 10, null, Share.FOLLOWER, Sort.RECENT, null)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), null, 10, null, Share.FOLLOWER, Sort.RECENT, null
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(4, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - 모든 값을 줬을 때 제대로 작동하는지 - oldVersion")
	void getShareGoals_Ok6() {
		// given
		List<Goal> goalList = List.of(user2GoalFollower, user1GoalFollower);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(8L, Status.ACTIVE)).willReturn(Optional.of(user3GoalFollower));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), user3GoalFollower, 8L, 6, Category.RETIREMENT, Share.FOLLOWER, Sort.RECENT, null)).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), 8L, 6, Category.RETIREMENT, Share.FOLLOWER, Sort.RECENT, null
		);

		//then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(goalResponseDtoList);
		assertEquals(4, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - 인기순으로 정렬했을 때 제대로 작동하는지 만약 댓글 수와 좋아요 수가 같다면 최신순으로 되는지")
	void getShareGoals_Ok7() {
		// given
		List<Goal> goalList = List.of(user2GoalAll, user1GoalAll, authUserGoalAll, user3GoalAll);
		List<ShareGoalResponseDto> responseDtoList = goalList.stream().map(ShareGoalResponseDto::from).toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(authUser.getId(), null, null, 10,
			Category.HOUSING, null, Sort.POPULAR, null)).willReturn(goalList);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(authUser.getId(), null, 10,
			Category.HOUSING,
			null, Sort.POPULAR, null);

		// then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(responseDtoList);
		assertEquals(7, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - 기간을 줬을 때 제대로 작동하는지")
	void getShareGoals_Ok8() {
		// given
		List<Goal> goalList = List.of(user1GoalAll, authUserGoalAll, user2GoalAll);
		List<ShareGoalResponseDto> responseDtoList = goalList.stream().map(ShareGoalResponseDto::from).toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndFilters(authUser.getId(), null, null, 10,
			Category.HOUSING, null, Sort.RECENT, Period.ofDays(7))).willReturn(goalList);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(authUser.getId(), null, 10,
			Category.HOUSING,
			null, Sort.RECENT, Period.ofDays(7));

		// then
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(responseDtoList);
		assertEquals(5, result.getNextCursor());
	}

	@Test
	@DisplayName("공유 목표 조회 성공 - 모든 값을 줬을 때 제대로 작동하는지 - newVersion")
	void getShareGoals_Ok9() {
		// given
		List<Goal> goalList = List.of(user1GoalFollower);
		List<ShareGoalResponseDto> goalResponseDtoList = goalList
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(6L, Status.ACTIVE)).willReturn(Optional.of(user2GoalFollower));
		given(goalRepository.findByAuthIdAndCursorAndFilters(
			authUser.getId(), user2GoalFollower, 6L, 1, Category.RETIREMENT, Share.FOLLOWER, Sort.POPULAR,
			Period.ofDays(7))).willReturn(goalList
		);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.getShareGoals(
			authUser.getId(), 6L, 1, Category.RETIREMENT, Share.FOLLOWER, Sort.POPULAR, Period.ofDays(7)
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
			authUser.getId(), null, 10, null, null, Sort.RECENT, null)
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

	@Test
	@DisplayName("키워드로 공유 목표 조회 성공")
	void searchShareGoals_Ok() {
		// given
		List<Goal> goalList = List.of(user2GoalFollower, user1GoalFollower);
		List<ShareGoalResponseDto> responseDtoList = goalList.stream().map(ShareGoalResponseDto::from).toList();
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(goalRepository.findByAuthIdAndCursorAndTitleContaining(authUser.getId(), null, 10, "은퇴"))
			.willReturn(goalList);

		// when
		ShareGoalCursorResponseDto result = shareGoalService.searchShareGoals(
			authUser.getId(), null, 10, "은퇴");

		// then
		assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(responseDtoList);
		assertEquals(4, result.getNextCursor());
	}

	@Test
	@DisplayName("키워드로 공유 목표 조회 실패 - 로그인 유저 x")
	void searchShareGoals_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.empty());
		// when, then
		CustomException exception = assertThrows(CustomException.class,
			() -> shareGoalService.searchShareGoals(authUser.getId(), null, 10, "은퇴"));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}


	@Test
	@DisplayName("검색된 키워드 증가 성공")
	void plusSearchKeyword_Ok() {
		// given
		ShareGoalSearchRequestDto requestDto = ShareGoalSearchRequestDto.builder()
			.keyword("은퇴")
			.title("은퇴 마렵다")
			.tags(List.of("은퇴"))
			.category(Category.RETIREMENT)
			.build();
		String category = requestDto.getCategory().toString();
		String accurateKeyword = "은퇴";

		String key = "search:keywords";

		String value = String.format("%s:%s", category, accurateKeyword);

		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Double> scoreCaptor = ArgumentCaptor.forClass(Double.class);

		// when
		shareGoalService.plusSearchKeyword(authUser.getId(), requestDto);

		// then
		verify(zSetOperations).incrementScore(keyCaptor.capture(), valueCaptor.capture(), scoreCaptor.capture());
		assertEquals(key, keyCaptor.getValue());
		assertEquals(value, valueCaptor.getValue());
		assertEquals(1, scoreCaptor.getValue());
	}

	@Test
	@DisplayName("검색된 키워드 증가 실패 - 로그인 유저 x")
	void plusSearchKeyword_Fail() {
		// given
		ShareGoalSearchRequestDto requestDto = ShareGoalSearchRequestDto.builder()
			.keyword("은퇴")
			.title("은퇴")
			.tags(List.of("은퇴"))
			.category(Category.RETIREMENT)
			.build();

		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.empty());

		// when then
		CustomException exception = assertThrows(CustomException.class,
			() -> shareGoalService.plusSearchKeyword(authUser.getId(), requestDto));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("검색된 키워드 증가 성공? - null인 경우")
	void plusSearchKeyword_Ok1() {
		// given
		ShareGoalSearchRequestDto requestDto = ShareGoalSearchRequestDto.builder()
			.keyword("주거")
			.title("은퇴 마렵다")
			.tags(List.of("은퇴"))
			.category(Category.RETIREMENT)
			.build();
		String category = requestDto.getCategory().toString();
		String keyword = requestDto.getKeyword();
		String accurateKeyword = null;
		String key = "search:keywords";
		String value = String.format("%s:%s", category, accurateKeyword);
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));

		// when
		shareGoalService.plusSearchKeyword(authUser.getId(), requestDto);

		// then
		verify(zSetOperations, never()).incrementScore(key, value, 1);
	}

	@Test
	@DisplayName("인기 검색어 Top 10 조회 성공")
	void searchTop10Keyword_Ok() {
		// given
		String key = "search:keywords";
		ZSetOperations.TypedTuple<String> fake = mock(ZSetOperations.TypedTuple.class);
		given(fake.getValue()).willReturn("RETIREMENT:은퇴");
		given(fake.getScore()).willReturn(1.0);
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 9)).willReturn(Set.of(fake));
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));


		// when
		List<ShareGoalSearchResponseDto> result = shareGoalService.searchTop10Keyword(authUser.getId());

		// then
		assertEquals(1, result.size());
		assertEquals("RETIREMENT:은퇴", result.getFirst().getKeyword());
		assertEquals(1.0, result.getFirst().getScore());
	}

	@Test
	@DisplayName("인기 검색어 Top 10 조회 실패 - 로그인 유저 x")
	void searchTop10Keyword_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.empty());

		// when then
		CustomException exception = assertThrows(CustomException.class,
			() -> shareGoalService.searchTop10Keyword(authUser.getId()));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("자동완성 조회 성공")
	void searchAutocomplete_Ok() {
		// given
		String key = "search:keywords";
		Set<String> fake = Set.of("category:tag", "category:tt");
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.of(authUser));
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(redisTemplate.opsForZSet().reverseRange(key, 0, -1)).willReturn(fake);

		// when
		List<String> result = shareGoalService.searchAutocomplete(authUser.getId(), "t");

		// then
		assertEquals(2, result.size());
		assertThat(result).contains("tt", "tag");
	}

	@Test
	@DisplayName("자동완성 조회 실패 - 로그인 유저 x")
	void searchAutocomplete_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(authUser.getId())).willReturn(Optional.empty());

		// when then
		CustomException exception = assertThrows(CustomException.class,
			() -> shareGoalService.searchAutocomplete(authUser.getId(), "t"));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}
}