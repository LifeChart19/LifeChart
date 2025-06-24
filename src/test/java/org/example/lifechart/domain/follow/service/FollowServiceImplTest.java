package org.example.lifechart.domain.follow.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowersResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowingResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowRequestResponseDto;
import org.example.lifechart.domain.follow.entity.Follow;
import org.example.lifechart.domain.follow.repository.FollowRepository;
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
class FollowServiceImplTest {

	@Mock
	private FollowRepository followRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private FollowServiceImpl followService;
	Long me;
	Long you;
	User authUser;
	User user;
	Follow follow;
	@BeforeEach
	void setUp() {
		me = 1L;
		you = 2L;
		authUser = new User(me, "name", "email", "password", "nickname", "men", "0",
			"job", false, null, LocalDate.now(), "user", "provider", "0");
		user = new User(you, "name1", "email1", "password1", "nickname1", "men1", "1",
			"job", false, null, LocalDate.now(), "user", "provider1", "1");
		follow = Follow.createFollow(authUser, user);
		ReflectionTestUtils.setField(follow, "id", 1L);
	}

	@Test
	@DisplayName("팔로우 요청 성공")
	void followRequest_OK() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.of(user));
		given(followRepository.existsByRequesterIdAndReceiverId(me, you)).willReturn(false);
		given(followRepository.save(any(Follow.class))).willReturn(follow);

		//when
		FollowRequestResponseDto result = followService.followRequest(me, you);

		//then
		assertEquals(follow.getId(), result.getId());
	}

	@Test
	@DisplayName("팔로우 중복 요청")
	void followRequest_Fail() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.of(user));
		given(followRepository.existsByRequesterIdAndReceiverId(me, you)).willReturn(true);

		//when then
		CustomException exception = assertThrows(CustomException.class, () -> followService.followRequest(me, you));
		assertEquals("팔로우가 이미 존재합니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("로그인 유저 존재 x")
	void followRequest_Fail1() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());

		//when then
		CustomException exception = assertThrows(CustomException.class, () -> followService.followRequest(me, you));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("팔로우할 유저 존재 x")
	void followRequest_Fail2() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.empty());

		//when then
		CustomException exception = assertThrows(CustomException.class, () -> followService.followRequest(me, you));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}



	@Test
	@DisplayName("팔로워 조회 성공")
	void getFollowers_Ok() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.of(user));
		Pageable pageable = PageRequest.of(0, 10);
		PageImpl<Follow> mockPage = new PageImpl<>(List.of(follow));
		given(followRepository.findByReceiverId(you, pageable)).willReturn(mockPage);

		//when
		Page<FollowGetFollowersResponseDto> result = followService.getFollowers(me, you, 1, 10);
		//then
		assertEquals(follow.getId(), result.getContent().getFirst().getId());
		assertEquals(1, result.getTotalElements());
		assertEquals(me, result.getContent().getFirst().getRequesterId());
	}

	@Test
	@DisplayName("로그인 유저 존재 x")
	void getFollowers_Fail1() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());

		//when then
		CustomException exception = assertThrows(CustomException.class,
			() -> followService.getFollowers(me, you, 1, 10));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("조회할 대상 유저 존재 x")
	void getFollowers_Fail2() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.empty());

		//when then
		CustomException exception = assertThrows(CustomException.class,
			() -> followService.getFollowers(me, you, 1, 10));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("팔로잉 조회 성공")
	void getFollowing_Ok() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.of(user));
		Pageable pageable = PageRequest.of(0, 10);
		PageImpl<Follow> mockPage = new PageImpl<>(List.of(follow));
		given(followRepository.findByRequesterId(me, pageable)).willReturn(mockPage);

		//when
		Page<FollowGetFollowingResponseDto> result = followService.getFollowing(you, me, 1, 10);
		//then
		assertEquals(follow.getId(), result.getContent().getFirst().getId());
		assertEquals(1, result.getTotalElements());
		assertEquals(you, result.getContent().getFirst().getReceiverId());
	}

	@Test
	@DisplayName("로그인 유저 존재 x")
	void getFollowing_Fail1() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.empty());

		//when then
		CustomException exception = assertThrows(CustomException.class,
			() -> followService.getFollowing(you, me, 1, 10));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("조회할 대상 유저 존재 x")
	void getFollowing_Fail2() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.of(user));
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());


		//when then
		CustomException exception = assertThrows(CustomException.class,
			() -> followService.getFollowing(you, me, 1, 10));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("팔로우 단건 조회 성공")
	void getFollow_Ok() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(followRepository.findById(anyLong())).willReturn(Optional.of(follow));

		//when
		FollowGetResponseDto result = followService.getFollow(me, 1L);

		//then
		assertEquals(follow.getId(), result.getId());
		assertEquals(me, result.getRequesterId());
		assertEquals(you, result.getReceiverId());
	}

	@Test
	@DisplayName("팔로우 단건 조회 실패")
	void getFollow_Fail() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(followRepository.findById(999L)).willReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class, () -> followService.getFollow(me, 999L));
		assertEquals("팔로우를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("로그인 유저 존재 x")
	void getFollow_Fail1() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());

		//when then
		CustomException exception = assertThrows(CustomException.class, () -> followService.getFollow(me, 999L));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("팔로우 취소 성공")
	void followCancel_Ok() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.of(user));
		given(followRepository.findByRequesterIdAndReceiverId(me, you)).willReturn(Optional.of(follow));

		// when
		followService.followCancel(me, you);

		// then
		verify(followRepository).delete(follow);

	}

	@Test
	@DisplayName("팔로우 취소 실패")
	void followCancel_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.of(user));
		given(followRepository.findByRequesterIdAndReceiverId(me, you)).willReturn(Optional.empty());
		// then
		CustomException exception = assertThrows(CustomException.class, () -> followService.followCancel(me, you));
		assertEquals("팔로우를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("로그인 유저 x")
	void followCancel_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());
		// then
		CustomException exception = assertThrows(CustomException.class, () -> followService.followCancel(me, you));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("대상 유저 x")
	void followCancel_Fail2() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(userRepository.findByIdAndDeletedAtIsNull(you)).willReturn(Optional.empty());
		// then
		CustomException exception = assertThrows(CustomException.class, () -> followService.followCancel(me, you));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

}