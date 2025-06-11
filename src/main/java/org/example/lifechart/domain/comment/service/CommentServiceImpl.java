package org.example.lifechart.domain.comment.service;

import java.util.List;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.dto.response.CommentGetResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentPageResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentResponseDto;
import org.example.lifechart.domain.comment.entity.Comment;
import org.example.lifechart.domain.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;

	@Transactional
	@Override
	public CommentResponseDto createComment(Long goalId, CommentRequestDto commentRequestDto) {
		// 로그인 유저 존재 여부 검증
		// 목표 존재 여부 검증
		String contents = commentRequestDto.getContents();
		// 로그인 유저 대체용
		Long me = 1L;
		Comment comment = Comment.createComment(me, goalId, contents);
		Comment savedComment = commentRepository.save(comment);
		return CommentResponseDto.from(savedComment);
	}

	@Transactional
	@Override
	public CommentPageResponseDto getComments(Long goalId, Long cursorId, int size) {
		// 로그인 유저 존재 여부 검증
		// 목표 존재 여부 검증
		List<CommentGetResponseDto> list = commentRepository.findByIdAndCursor(goalId, cursorId, size).stream()
			.map(CommentGetResponseDto::from).toList();
		Long nextCursor = list.isEmpty() ? null : list.getLast().getId() - 1;
		return CommentPageResponseDto.builder()
			.content(list)
			.nextCursor(nextCursor)
			.build();
	}

	@Transactional
	@Override
	public CommentGetResponseDto getComment(Long commentId) {
		// 로그인 유저 존재 여부 검증
		Comment findedComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

		return CommentGetResponseDto.from(findedComment);
	}

	@Transactional
	@Override
	public CommentGetResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto) {
		// 로그인 유저 존재 여부 검증
		// 로그인 대체용
		Long me = 1L;
		Comment findedComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
		if (me != findedComment.getUserId()) {
			throw new CustomException(ErrorCode.COMMENT_FORBIDDEN);
		}
		String updateContents = commentRequestDto.getContents();
		findedComment.updateContents(updateContents);
		return CommentGetResponseDto.from(findedComment);
	}

	@Transactional
	@Override
	public void deleteComment(Long commentId) {
		// 로그인 유저 존재 여부 검증
		// 로그인 대체용
		Long me = 1L;
		Comment findedComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
		if (me != findedComment.getUserId()) {
			throw new CustomException(ErrorCode.COMMENT_FORBIDDEN);
		}
		commentRepository.delete(findedComment);
	}
}
