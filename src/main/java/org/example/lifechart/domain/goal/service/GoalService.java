package org.example.lifechart.domain.goal.service;

import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.response.GoalResponseDto;
import org.example.lifechart.domain.user.entity.User;

public interface GoalService {
	GoalResponseDto createGoal(GoalCreateRequest requestDto, User user);
}
