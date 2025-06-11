package org.example.lifechart.domain.follow.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowersResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetFollowingResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowGetResponseDto;
import org.example.lifechart.domain.follow.dto.response.FollowRequestResponseDto;
import org.example.lifechart.domain.follow.entity.Follow;
import org.example.lifechart.domain.follow.repository.FollowRepository;
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

	@InjectMocks
	private FollowServiceImpl followService;

	Long me = 1L;
	Long you = 2L;

	@Test
	@DisplayName("팔로우 요청 성공")
	void followRequest_OK() {
		//given
		given(followRepository.existsByRequestIdAndReceiverId(me, you)).willReturn(false);
		Follow follow = Follow.createFollow(me, you);
		ReflectionTestUtils.setField(follow, "id", 1L);
		given(followRepository.save(any(Follow.class))).willReturn(follow);

		//when
		FollowRequestResponseDto result = followService.followRequest(you);

		//then
		assertEquals(follow.getId(), result.getId());
	}

	@Test
	@DisplayName("팔로우 중복 요청")
	void followRequest_Fail() {
		//given
		given(followRepository.existsByRequestIdAndReceiverId(me, you)).willReturn(true);

		//when
		//then
		CustomException exception = assertThrows(CustomException.class, () -> followService.followRequest(you));
		assertEquals("팔로우가 이미 존재합니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("팔로워 조회 성공")
	void getFollowers_Ok() {
		//given
		Pageable pageable = PageRequest.of(0, 10);
		Follow follow = Follow.createFollow(me, you);
		ReflectionTestUtils.setField(follow, "id", 1L);
		PageImpl<Follow> mockPage = new PageImpl<>(List.of(follow));
		given(followRepository.findByReceiverId(you, pageable)).willReturn(mockPage);

		//when
		Page<FollowGetFollowersResponseDto> result = followService.getFollowers(you, 1, 10);
		//then
		assertEquals(follow.getId(), result.getContent().getFirst().getId());
		assertEquals(1, result.getTotalElements());
		assertEquals(me, result.getContent().getFirst().getRequestId());
	}

	@Test
	@DisplayName("팔로잉 조회 성공")
	void getFollowing_Ok() {
		//given
		Pageable pageable = PageRequest.of(0, 10);
		Follow follow = Follow.createFollow(me, you);
		ReflectionTestUtils.setField(follow, "id", 1L);
		PageImpl<Follow> mockPage = new PageImpl<>(List.of(follow));
		given(followRepository.findByRequestId(me, pageable)).willReturn(mockPage);

		//when
		Page<FollowGetFollowingResponseDto> result = followService.getFollowing(me, 1, 10);
		//then
		assertEquals(follow.getId(), result.getContent().getFirst().getId());
		assertEquals(1, result.getTotalElements());
		assertEquals(you, result.getContent().getFirst().getReceiverId());
	}

	@Test
	@DisplayName("팔로우 단건 조회 성공")
	void getFollow_Ok() {
		//given
		Follow follow = Follow.createFollow(me, you);
		ReflectionTestUtils.setField(follow, "id", 1L);
		given(followRepository.findById(anyLong())).willReturn(Optional.of(follow));

		//when
		FollowGetResponseDto result = followService.getFollow(1L);

		//then
		assertEquals(follow.getId(), result.getId());
		assertEquals(me, result.getRequestId());
		assertEquals(you, result.getReceiverId());
	}

	@Test
	@DisplayName("팔로우 단건 조회 실패")
	void getFollow_Fail() {
		//given
		given(followRepository.findById(999L)).willReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class, () -> followService.getFollow(999L));
		assertEquals("팔로우를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("팔로우 취소 성공")
	void followCancel_Ok() {
		// given
		Follow follow = Follow.createFollow(me, you);
		given(followRepository.findByRequestIdAndReceiverId(me, you)).willReturn(Optional.of(follow));

		// when
		followService.followCancel(you);

		// then
		assertNotNull(follow);
		verify(followRepository).delete(follow);

	}

	@Test
	@DisplayName("팔로우 취소 실패")
	void followCancel_Fail() {
		// given
		given(followRepository.findByRequestIdAndReceiverId(me, you)).willReturn(Optional.empty());
		// then
		CustomException exception = assertThrows(CustomException.class, () -> followService.followCancel(you));
		assertEquals("팔로우를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());

	}
}