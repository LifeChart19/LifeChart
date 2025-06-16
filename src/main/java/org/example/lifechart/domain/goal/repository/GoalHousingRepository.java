package org.example.lifechart.domain.goal.repository;

import java.util.Optional;

import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalHousingRepository extends JpaRepository<GoalHousing, Long> {
	Optional<GoalHousing> findByGoalId(Long goalId);
}
