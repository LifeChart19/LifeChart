package org.example.lifechart.domain.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.dto.response.CommentGetResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentPageResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentResponseDto;
import org.example.lifechart.domain.comment.entity.Comment;
import org.example.lifechart.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentServiceImpl commentService;
	Long me;
	Long goalId;
	String contents;
	CommentRequestDto commentRequestDto;
	Comment comment;

	@BeforeEach
	void setUp() {
		me = 1L;
		goalId = 1L;
		contents = "hi";
		commentRequestDto = CommentRequestDto.builder().contents(contents).build();
		comment = Comment.createComment(me, goalId, contents);
		ReflectionTestUtils.setField(comment, "id", 1L);
	}

	@Test
	@DisplayName("댓글 생성 성공")
	void createComment_Ok() {
		// given
		given(commentRepository.save(any(Comment.class))).willReturn(comment);

		// when
		CommentResponseDto result = commentService.createComment(goalId, commentRequestDto);

		// then
		assertEquals(comment.getId(), result.getId());
	}

	@Test
	@DisplayName("댓글 목록 성공")
	void getComments_Ok() {
		// given
		List<Comment> comments = List.of(comment);
		given(commentRepository.findByIdAndCursor(goalId, null, 10)).willReturn(comments);

		// when
		CommentPageResponseDto result = commentService.getComments(goalId, null, 10);

		// then
		assertEquals(comment.getId(), result.getContent().getFirst().getId());
		assertEquals(comment.getUserId(), result.getContent().getFirst().getUserId());
		assertEquals(comment.getGoalId(), result.getContent().getFirst().getGoalId());
		assertEquals(comment.getContents(), result.getContent().getFirst().getContents());
		assertEquals(0, result.getNextCursor());
	}

	@Test
	@DisplayName("댓글 목록이 null")
	void getComments_Ok1() {
		List<Comment> list = List.of();
		List<CommentGetResponseDto> responseDtoList = List.of();
		// given
		given(commentRepository.findByIdAndCursor(goalId, null, 10)).willReturn(list);

		// when
		CommentPageResponseDto result = commentService.getComments(goalId, null, 10);

		// then
		assertEquals(responseDtoList, result.getContent());
		assertNull(result.getNextCursor());
	}

	@Test
	@DisplayName("댓글 단건 조회 성공")
	void getComment_Ok() {
		// given
		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

		// when
		CommentGetResponseDto result = commentService.getComment(1L);

		// then
		assertEquals(comment.getId(), result.getId());
		assertEquals(comment.getUserId(), result.getUserId());
		assertEquals(comment.getGoalId(), result.getGoalId());
		assertEquals(comment.getContents(), result.getContents());
	}

	@Test
	@DisplayName("댓글 단건 조회 실패")
	void getComment_Fail() {
		// given
		given(commentRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> commentService.getComment(999L));
		assertEquals("댓글을 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 수정 성공")
	void updateComment_Ok() {
		//given
		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
		String newContents = "bye";
		commentRequestDto = CommentRequestDto.builder().contents(newContents).build();
		comment.updateContents(newContents);

		// when
		CommentGetResponseDto result = commentService.updateComment(1L, commentRequestDto);

		//then
		assertEquals(comment.getId(), result.getId());
		assertEquals(comment.getUserId(), result.getUserId());
		assertEquals(comment.getGoalId(), result.getGoalId());
		assertEquals(comment.getContents(), result.getContents());
	}

	@Test
	@DisplayName("댓글 수정 실패 - 댓글 존재 x")
	void updateComment_Fail() {
		//given
		given(commentRepository.findById(1L)).willReturn(Optional.empty());
		String newContents = "bye";
		commentRequestDto = CommentRequestDto.builder().contents(newContents).build();

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.updateComment(1L, commentRequestDto));
		assertEquals("댓글을 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 수정 실패 - 댓글 주인 x")
	void updateComment_Fail1() {
		//given
		Comment anotherComment = Comment.createComment(2L, goalId, contents);
		ReflectionTestUtils.setField(anotherComment, "id", 2L);
		given(commentRepository.findById(2L)).willReturn(Optional.of(anotherComment));
		String newContents = "bye";
		commentRequestDto = CommentRequestDto.builder().contents(newContents).build();

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.updateComment(2L, commentRequestDto));
		assertEquals("댓글은 본인만 수정 및 삭제할 수 있습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 삭제 성공")
	void deleteComment_Ok() {
		//given
		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

		//when
		commentService.deleteComment(1L);

		//then
		verify(commentRepository).delete(comment);
	}

	@Test
	@DisplayName("댓글 삭제 실패 - 댓글 존재 x")
	void deleteComment_Fail() {
		//given
		given(commentRepository.findById(999L)).willReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.deleteComment(999L));
		assertEquals("댓글을 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 삭제 실패 - 댓글 본인 x")
	void deleteComment_Fail1() {
		//given
		Comment anotherComment = Comment.createComment(2L, goalId, contents);
		ReflectionTestUtils.setField(anotherComment, "id", 2L);
		given(commentRepository.findById(2L)).willReturn(Optional.of(anotherComment));

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.deleteComment(2L));
		assertEquals("댓글은 본인만 수정 및 삭제할 수 있습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}
}