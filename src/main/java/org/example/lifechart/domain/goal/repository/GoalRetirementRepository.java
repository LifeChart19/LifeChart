package org.example.lifechart.domain.goal.repository;

import java.util.Optional;

import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRetirementRepository extends JpaRepository<GoalRetirement, Long> {
	Optional<GoalRetirement> findByGoalId(Long Id);
}
