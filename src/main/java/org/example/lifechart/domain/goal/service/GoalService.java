package org.example.lifechart.domain.goal.service;

import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalSearchCondition;
import org.example.lifechart.domain.goal.dto.request.GoalUpdateRequest;
import org.example.lifechart.domain.goal.dto.response.CursorPageResponse;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.dto.response.GoalSummaryResponse;

public interface GoalService {
	GoalResponse createGoal(GoalCreateRequest request, Long userId);
	GoalInfoResponse findGoal(Long goalId, Long userId);
	void deleteGoal(Long goalId, Long userId);
	GoalResponse updateGoal(GoalUpdateRequest request, Long goalId, Long UserId);
	CursorPageResponse<GoalSummaryResponse> findMyGoals(Long userId, GoalSearchCondition condition);
}
