package org.example.lifechart.domain.comment.service;

import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.dto.response.CommentGetResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentPageResponseDto;
import org.example.lifechart.domain.comment.dto.response.CommentResponseDto;

public interface CommentService {
	CommentResponseDto createComment(Long goalId, CommentRequestDto commentRequestDto);

	CommentPageResponseDto getComments(Long goalId, Long cursorId, int size);

	CommentGetResponseDto getComment(Long commentId);

	CommentGetResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto);

	void deleteComment(Long commentId);

}
