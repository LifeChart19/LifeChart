package org.example.lifechart.domain.comment.service;

import java.util.List;

import org.example.lifechart.common.enums.ErrorCode;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final GoalRepository goalRepository;

	@Transactional
	@Override
	public CommentResponseDto createComment(Long authId, Long goalId, CommentRequestDto commentRequestDto) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		// 목표 존재 여부 검증
		Goal findedGoal = validGoal(goalId);
		String contents = commentRequestDto.getContents();
		Comment comment = Comment.createComment(findedUser, findedGoal, contents);
		Comment savedComment = commentRepository.save(comment);
		return CommentResponseDto.from(savedComment);
	}

	@Transactional
	@Override
	public CommentCursorResponseDto getComments(Long authId, Long goalId, Long cursorId, int size) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		// 목표 존재 여부 검증
		Goal findedGoal = validGoal(goalId);
		List<CommentGetResponseDto> list = commentRepository.findByIdAndCursor(findedGoal.getId(), cursorId, size)
			.stream()
			.map(CommentGetResponseDto::from)
			.toList();
		return CommentCursorResponseDto.from(list);
	}

	@Transactional
	@Override
	public CommentGetResponseDto getComment(Long authId, Long commentId) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		Comment findedComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

		return CommentGetResponseDto.from(findedComment);
	}

	@Transactional
	@Override
	public CommentGetResponseDto updateComment(Long authId, Long commentId, CommentRequestDto commentRequestDto) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		Comment findedComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
		if (findedUser.getId() != findedComment.getUser().getId()) {
			throw new CustomException(ErrorCode.COMMENT_FORBIDDEN);
		}
		String updateContents = commentRequestDto.getContents();
		findedComment.updateContents(updateContents);
		return CommentGetResponseDto.from(findedComment);
	}

	@Transactional
	@Override
	public void deleteComment(Long authId, Long commentId) {
		// 로그인 유저 존재 여부 검증
		User findedUser = validUser(authId);
		Comment findedComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
		if (findedUser.getId() != findedComment.getUser().getId()) {
			throw new CustomException(ErrorCode.COMMENT_FORBIDDEN);
		}
		commentRepository.delete(findedComment);
	}

	private User validUser(Long userId) {
		return userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	private Goal validGoal(Long goalId) {
		return goalRepository.findByIdAndStatus(goalId, Status.ACTIVE)
			.orElseThrow(()-> new CustomException(ErrorCode.GOAL_NOT_FOUND));
	}
}
