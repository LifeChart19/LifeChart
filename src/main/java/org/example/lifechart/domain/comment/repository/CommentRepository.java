package org.example.lifechart.domain.comment.repository;

import java.util.Optional;

import org.example.lifechart.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {
	@Query("SELECT c.goal.id FROM Comment c WHERE c.id = :commentId")
	Optional<Long> findGoalIdByCommentId(@Param("commentId") Long commentId);
}
