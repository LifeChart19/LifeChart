package org.example.lifechart.domain.goal.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Status;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalSummaryResponse {

	private Long id;
	private String title;
	private Long targetAmount;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Status status;
	private List<String> tags;

	public static GoalSummaryResponse from(Goal goal) {
		return GoalSummaryResponse.builder()
			.id(goal.getId())
			.title(goal.getTitle())
			.targetAmount(goal.getTargetAmount())
			.startAt(goal.getStartAt())
			.endAt(goal.getEndAt())
			.status(goal.getStatus())
			.tags(goal.getTags())
			.build();
	}

}
