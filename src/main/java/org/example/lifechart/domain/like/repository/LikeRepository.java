package org.example.lifechart.domain.like.repository;

import java.util.Optional;

import org.example.lifechart.domain.like.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByUserIdAndGoalId(Long me, Long goalId);

	Optional<Like> findByUserIdAndGoalId(Long me, Long goalId);

	Page<Like> findByGoalId(Long goalId, Pageable pageable);
}
