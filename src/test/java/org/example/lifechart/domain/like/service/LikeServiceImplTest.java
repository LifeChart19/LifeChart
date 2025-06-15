package org.example.lifechart.domain.like.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.example.lifechart.domain.like.entity.Like;
import org.example.lifechart.domain.like.repository.LikeRepository;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
	@Mock
	private LikeRepository likeRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private GoalRepository goalRepository;

	@InjectMocks
	private LikeServiceImpl likeService;
	Long me;
	Long goalId;
	Like like;
	User authUser;
	User user;
	Goal goal;
	Status status;

	@BeforeEach
	void setUp() {
		me = 1L;
		goalId = 1L;
		status = Status.ACTIVE;
		authUser = new User(me, "email", "password", "nickname", "men", "0",
			"job", false, null, LocalDate.now(), "user", "provider", "0");
		user = new User(2L, "email2", "password2", "nickname2", "men", "2",
			"job", false, null, LocalDate.now(), "user", "provider2", "2");
		goal = Goal.builder().id(goalId).user(authUser).status(Status.ACTIVE).build();
		like = Like.createLike(authUser, goal);
		ReflectionTestUtils.setField(like, "id", 1L);
	}


	@Test
	@DisplayName("좋아요++ 성공")
	void plusLike_Ok() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.of(goal));
		given(likeRepository.existsByUserIdAndGoalId(me, goalId)).willReturn(false);
		given(likeRepository.save(any(Like.class))).willReturn(like);

		// when
		LikeResponseDto result = likeService.plusLike(authUser.getId(), goal.getId());

		//then
		assertEquals(like.getId(), result.getId());
	}

	@Test
	@DisplayName("좋아요++ 실패 - 좋아요 중복")
	void plusLike_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.of(goal));
		given(likeRepository.existsByUserIdAndGoalId(me, goalId)).willReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.plusLike(authUser.getId(), goalId));
		assertEquals("좋아요를 이미 눌렀습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요++ 실패 - 로그인 유저 x")
	void plusLike_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());
		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.plusLike(authUser.getId(), goalId));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요++ 실패 - 목표 존재 x")
	void plusLike_Fail2() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.empty());
		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.plusLike(authUser.getId(), goalId));
		assertEquals("목표가 존재하지 않습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 목록 조회 성공")
	void getLikes_Ok() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.of(goal));
		Pageable pageable = PageRequest.of(0, 10);
		PageImpl<Like> mockPage = new PageImpl<>(List.of(like));
		given(likeRepository.findByGoalId(goalId, pageable)).willReturn(mockPage);

		// when
		Page<LikeGetResponseDto> result = likeService.getLikes(authUser.getId(), goalId, 1, 10);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals(like.getId(), result.getContent().getFirst().getId());
		assertEquals(like.getUser().getId(), result.getContent().getFirst().getUserId());
		assertEquals(like.getGoal().getId(), result.getContent().getFirst().getGoalId());
	}

	@Test
	@DisplayName("좋아요 목록 조회 실패 - 로그인 유저 x")
	void getLikes_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());
		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.getLikes(authUser.getId(), goalId, 1, 10));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 목록 조회 실패 - 목표 존재 x")
	void getLikes_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.empty());
		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.getLikes(authUser.getId(), goalId, 1, 10));
		assertEquals("목표가 존재하지 않습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 단건 조회 성공")
	void getLike_Ok() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(likeRepository.findById(1L)).willReturn(Optional.of(like));

		// when
		LikeGetResponseDto result = likeService.getLike(authUser.getId(), 1L);

		// then
		assertEquals(like.getId(), result.getId());
		assertEquals(like.getUser().getId(), result.getUserId());
		assertEquals(like.getGoal().getId(), result.getGoalId());
	}

	@Test
	@DisplayName("좋아요 단건 조회 실패")
	void getLike_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(likeRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.getLike(authUser.getId(), 999L));
		assertEquals("좋아요를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 단건 조회 실패 - 로그인 유저 x")
	void getLike_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.getLike(authUser.getId(), 1L));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 취소 성공")
	void deleteLike() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(likeRepository.findById(anyLong())).willReturn(Optional.of(like));

		// when
		likeService.deleteLike(authUser.getId(), 1L);

		//then
		verify(likeRepository).delete(like);
	}

	@Test
	@DisplayName("좋아요 취소 실패 - 좋아요 존재 x")
	void deleteLike_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(likeRepository.findById(999L)).willReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.deleteLike(authUser.getId(), 999L));
		assertEquals("좋아요를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 취소 실패 - 좋아요 본인이 아닌 경우")
	void deleteLike_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		Like anotherLike = Like.createLike(user, goal);
		ReflectionTestUtils.setField(anotherLike, "id", 2L);
		given(likeRepository.findById(2L)).willReturn(Optional.of(anotherLike));

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> likeService.deleteLike(authUser.getId(), 2L));
		assertEquals("좋아요 취소는 본인만 가능합니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}
}