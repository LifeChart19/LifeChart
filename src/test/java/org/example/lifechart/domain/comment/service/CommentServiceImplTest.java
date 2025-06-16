package org.example.lifechart.domain.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.dto.response.CommentCursorResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentGetResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentResponseDto;
import org.example.lifechart.domain.comment.entity.Comment;
import org.example.lifechart.domain.comment.repository.CommentRepository;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
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

	@Mock
	private UserRepository userRepository;

	@Mock
	private GoalRepository goalRepository;

	@InjectMocks
	private CommentServiceImpl commentService;
	Long me;
	Long goalId;
	User authUser;
	User user;
	Goal goal;
	String contents;
	CommentRequestDto commentRequestDto;
	Comment comment;
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
		contents = "hi";
		commentRequestDto = CommentRequestDto.builder().contents(contents).build();
		comment = Comment.createComment(authUser, goal, contents);
		ReflectionTestUtils.setField(comment, "id", 1L);
	}

	@Test
	@DisplayName("댓글 생성 성공")
	void createComment_Ok() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.of(goal));
		given(commentRepository.save(any(Comment.class))).willReturn(comment);

		// when
		CommentResponseDto result = commentService.createComment(me, goalId, commentRequestDto);

		// then
		assertEquals(comment.getId(), result.getId());
	}

	@Test
	@DisplayName("댓글 생성 실패 - 로그인 유저 x")
	void createComment_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());

		// when then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.createComment(me, goalId, commentRequestDto));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 생성 실패 - 목표 존재 x")
	void createComment_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.empty());

		// when then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.createComment(me, goalId, commentRequestDto));
		assertEquals("목표가 존재하지 않습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 목록 성공")
	void getComments_Ok() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.of(goal));
		List<Comment> comments = List.of(comment);
		given(commentRepository.findByIdAndCursor(goalId, null, 10)).willReturn(comments);

		// when
		CommentCursorResponseDto result = commentService.getComments(me, goalId, null, 10);

		// then
		assertEquals(comment.getId(), result.getContent().getFirst().getId());
		assertEquals(comment.getUser().getId(), result.getContent().getFirst().getUserId());
		assertEquals(comment.getGoal().getId(), result.getContent().getFirst().getGoalId());
		assertEquals(comment.getContents(), result.getContent().getFirst().getContents());
		assertEquals(1, result.getNextCursor());
	}

	@Test
	@DisplayName("댓글 목록이 null")
	void getComments_Ok1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.of(goal));
		List<Comment> list = List.of();
		List<CommentGetResponseDto> responseDtoList = List.of();
		given(commentRepository.findByIdAndCursor(goalId, null, 10)).willReturn(list);

		// when
		CommentCursorResponseDto result = commentService.getComments(me, goalId, null, 10);

		// then
		assertEquals(responseDtoList, result.getContent());
		assertNull(result.getNextCursor());
	}

	@Test
	@DisplayName("댓글 목록 조회 실패 - 로그인 유저 x")
	void getComments_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());

		// when then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.getComments(me, goalId, null, 10));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 목록 조회 실패 - 목표 존재 x")
	void getComments_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(goalRepository.findByIdAndStatus(goalId, status)).willReturn(Optional.empty());
		// when then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.getComments(me, goalId, null, 10));
		assertEquals("목표가 존재하지 않습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 단건 조회 성공")
	void getComment_Ok() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

		// when
		CommentGetResponseDto result = commentService.getComment(me, 1L);

		// then
		assertEquals(comment.getId(), result.getId());
		assertEquals(comment.getUser().getId(), result.getUserId());
		assertEquals(comment.getGoal().getId(), result.getGoalId());
		assertEquals(comment.getContents(), result.getContents());
	}

	@Test
	@DisplayName("댓글 존재 x")
	void getComment_Fail() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(commentRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> commentService.getComment(me, 999L));
		assertEquals("댓글을 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("로그인 유저 x")
	void getComment_Fail1() {
		// given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> commentService.getComment(me, 999L));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 수정 성공")
	void updateComment_Ok() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
		String newContents = "bye";
		commentRequestDto = CommentRequestDto.builder().contents(newContents).build();
		comment.updateContents(newContents);

		// when
		CommentGetResponseDto result = commentService.updateComment(me, 1L, commentRequestDto);

		//then
		assertEquals(comment.getId(), result.getId());
		assertEquals(comment.getUser().getId(), result.getUserId());
		assertEquals(comment.getGoal().getId(), result.getGoalId());
		assertEquals(comment.getContents(), result.getContents());
	}

	@Test
	@DisplayName("댓글 수정 실패 - 댓글 존재 x")
	void updateComment_Fail() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(commentRepository.findById(1L)).willReturn(Optional.empty());
		String newContents = "bye";
		commentRequestDto = CommentRequestDto.builder().contents(newContents).build();

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.updateComment(me, 1L, commentRequestDto));
		assertEquals("댓글을 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 수정 실패 - 댓글 주인 x")
	void updateComment_Fail1() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		Comment anotherComment = Comment.createComment(user, goal, contents);
		ReflectionTestUtils.setField(anotherComment, "id", 2L);
		given(commentRepository.findById(2L)).willReturn(Optional.of(anotherComment));
		String newContents = "bye";
		commentRequestDto = CommentRequestDto.builder().contents(newContents).build();

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.updateComment(me, 2L, commentRequestDto));
		assertEquals("댓글은 본인만 수정 및 삭제할 수 있습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 수정 실패 - 로그인 유저 존재 x")
	void updateComment_Fail2() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());
		String newContents = "bye";
		commentRequestDto = CommentRequestDto.builder().contents(newContents).build();

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.updateComment(me, 1L, commentRequestDto));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 삭제 성공")
	void deleteComment_Ok() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

		//when
		commentService.deleteComment(me, 1L);

		//then
		verify(commentRepository).delete(comment);
	}

	@Test
	@DisplayName("댓글 삭제 실패 - 댓글 존재 x")
	void deleteComment_Fail() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		given(commentRepository.findById(999L)).willReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.deleteComment(me, 999L));
		assertEquals("댓글을 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 삭제 실패 - 댓글 본인 x")
	void deleteComment_Fail1() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.of(authUser));
		Comment anotherComment = Comment.createComment(user, goal, contents);
		ReflectionTestUtils.setField(anotherComment, "id", 2L);
		given(commentRepository.findById(2L)).willReturn(Optional.of(anotherComment));

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.deleteComment(me, 2L));
		assertEquals("댓글은 본인만 수정 및 삭제할 수 있습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}

	@Test
	@DisplayName("댓글 수정 실패 - 로그인 유저 존재 x")
	void updateComment_Fail3() {
		//given
		given(userRepository.findByIdAndDeletedAtIsNull(me)).willReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.deleteComment(me, 1L));
		assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
	}
}