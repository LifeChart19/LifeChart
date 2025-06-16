package org.example.lifechart.domain.goal.dto.response;

import org.example.lifechart.domain.goal.entity.Goal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalResponse {

	private final Long goalId;

	public static GoalResponse from(Goal savedGoal) {
		return GoalResponse.builder()
			.goalId(savedGoal.getId())
			.build();
	}
}
