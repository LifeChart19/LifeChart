package org.example.lifechart.domain.goal.service;

import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
import org.example.lifechart.domain.goal.dto.response.GoalResponseDto;
import org.example.lifechart.domain.user.entity.User;

public interface GoalService {
	GoalResponseDto createGoal(GoalCreateRequestDto requestDto, User user);
}
