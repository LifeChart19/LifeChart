package org.example.lifechart.domain.like.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.like.dto.response.LikeGetResponseDto;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.example.lifechart.domain.like.entity.Like;
import org.example.lifechart.domain.like.repository.LikeRepository;
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

	@InjectMocks
	private LikeServiceImpl likeService;
	Long me = 1L;
	Long goalId = 1L;

	@Test
	@DisplayName("좋아요++ 성공")
	void plusLike_Ok() {
		// given
		given(likeRepository.existsByUserIdAndGoalId(me, goalId)).willReturn(false);
		Like like = Like.createLike(me, goalId);
		ReflectionTestUtils.setField(like, "id", 1L);
		given(likeRepository.save(any(Like.class))).willReturn(like);

		// when
		LikeResponseDto result = likeService.plusLike(goalId);

		//then
		assertEquals(like.getId(), result.getId());
	}

	@Test
	@DisplayName("좋아요++ 실패")
	void plusLike_Fail() {
		// given
		given(likeRepository.existsByUserIdAndGoalId(me, goalId)).willReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> likeService.plusLike(goalId));
		assertEquals("좋아요를 이미 눌렀습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 목록 조회 성공")
	void getLikes_Ok() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Like like = Like.createLike(me, goalId);
		ReflectionTestUtils.setField(like, "id", 1L);
		PageImpl<Like> mockPage = new PageImpl<>(List.of(like));
		given(likeRepository.findByGoalId(goalId, pageable)).willReturn(mockPage);

		// when
		Page<LikeGetResponseDto> result = likeService.getLikes(goalId, 1, 10);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals(like.getId(), result.getContent().getFirst().getId());
		assertEquals(like.getUserId(), result.getContent().getFirst().getUserId());
		assertEquals(like.getGoalId(), result.getContent().getFirst().getGoalId());
	}

	@Test
	@DisplayName("좋아요 단건 조회 성공")
	void getLike_Ok() {
		// given
		Like like = Like.createLike(me, goalId);
		ReflectionTestUtils.setField(like, "id", 1L);
		given(likeRepository.findById(anyLong())).willReturn(Optional.of(like));

		// when
		LikeGetResponseDto result = likeService.getLike(1L);

		// then
		assertEquals(like.getId(), result.getId());
		assertEquals(like.getUserId(), result.getUserId());
		assertEquals(like.getGoalId(), result.getGoalId());
	}

	@Test
	@DisplayName("좋아요 단건 조회 실패")
	void getLike_Fail() {
		// given
		given(likeRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> likeService.getLike(999L));
		assertEquals("좋아요를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 취소 성공")
	void deleteLike() {
		// given
		Like like = Like.createLike(me, goalId);
		ReflectionTestUtils.setField(like, "id", 1L);
		given(likeRepository.findById(anyLong())).willReturn(Optional.of(like));

		// when
		likeService.deleteLike(1L);

		//then
		assertNotNull(like);
		verify(likeRepository).delete(like);
	}

	@Test
	@DisplayName("좋아요 취소 실패 - 좋아요 존재 x")
	void deleteLike_Fail() {
		// given
		given(likeRepository.findById(999L)).willReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class, () -> likeService.deleteLike(999L));
		assertEquals("좋아요를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("좋아요 취소 실패 - 좋아요 본인이 아닌 경우")
	void deleteLike_Fail1() {
		// given
		Like like = Like.createLike(2L, goalId);
		ReflectionTestUtils.setField(like, "id", 1L);
		given(likeRepository.findById(anyLong())).willReturn(Optional.of(like));

		//when & then
		CustomException exception = assertThrows(CustomException.class, () -> likeService.deleteLike(1L));
		assertEquals("좋아요 취소는 본인만 가능합니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}
}