package org.example.lifechart.domain.comment.repository;

import java.util.List;

import org.example.lifechart.domain.comment.entity.Comment;

public interface CustomCommentRepository {
	List<Comment> findByIdAndCursor(Long goalId, Long cursorId, int size);

}
