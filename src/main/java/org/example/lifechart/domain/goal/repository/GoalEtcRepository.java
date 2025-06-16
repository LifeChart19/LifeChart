package org.example.lifechart.domain.goal.repository;

import java.util.Optional;

import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalEtcRepository extends JpaRepository<GoalEtc, Long> {
	Optional<GoalEtc> findByGoalId(Long goalId);
}
