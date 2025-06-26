package org.example.lifechart.domain.goal.repository;

import java.util.List;

import org.example.lifechart.domain.goal.dto.request.GoalSearchCondition;
import org.example.lifechart.domain.goal.entity.Goal;

public interface CustomGoalRepository {
	List<Goal> searchGoalsWithCursor(Long userId, GoalSearchCondition condition);
}
