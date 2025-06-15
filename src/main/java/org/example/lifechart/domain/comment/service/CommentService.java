package org.example.lifechart.domain.comment.service;

import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.dto.response.CommentCursorResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentGetResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentResponseDto;

public interface CommentService {
	CommentResponseDto createComment(Long authId, Long goalId, CommentRequestDto commentRequestDto);

	CommentCursorResponseDto getComments(Long authId, Long goalId, Long cursorId, int size);

	CommentGetResponseDto getComment(Long authId, Long commentId);

	CommentGetResponseDto updateComment(Long authId, Long commentId, CommentRequestDto commentRequestDto);

	void deleteComment(Long authId, Long commentId);

}
