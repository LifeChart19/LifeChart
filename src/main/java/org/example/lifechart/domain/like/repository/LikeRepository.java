package org.example.lifechart.domain.like.repository;

import java.util.Optional;

import org.example.lifechart.domain.like.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByUserIdAndGoalId(Long me, Long goalId);

	Page<Like> findByGoalId(Long goalId, Pageable pageable);

	@Query("SELECT l.goal.id FROM Like l WHERE l.id = :likeId")
	Optional<Long> findGoalIdByLikeId(@Param("likeId") Long likeId);
}

