package org.example.lifechart.domain.goal.service;

import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.user.entity.User;

public interface GoalService {
	GoalResponse createGoal(GoalCreateRequest requestDto, Long userId);
	GoalInfoResponse findGoal(Long goalId, Long userId);
	void deleteGoal(Long goalId, Long userId);
}
