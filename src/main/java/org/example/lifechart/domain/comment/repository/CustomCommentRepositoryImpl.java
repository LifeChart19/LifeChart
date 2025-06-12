package org.example.lifechart.domain.comment.repository;

import java.util.List;

import org.example.lifechart.domain.comment.entity.Comment;
import org.example.lifechart.domain.comment.entity.QComment;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Comment> findByIdAndCursor(Long goalId, Long cursorId, int size) {
		QComment comment = QComment.comment;

		return jpaQueryFactory
			.selectFrom(comment)
			.where(comment.goalId.eq(goalId),
				cursorId != null ? comment.id.lt(cursorId) : null
			)
			.orderBy(comment.id.desc())
			.limit(size)
			.fetch();
	}
}
