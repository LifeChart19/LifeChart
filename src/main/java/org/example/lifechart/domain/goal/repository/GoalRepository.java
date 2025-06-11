package org.example.lifechart.domain.goal.repository;

import org.example.lifechart.domain.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
