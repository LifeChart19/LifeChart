package org.example.lifechart.domain.goal.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Status;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalSummaryResponse {

	private String title;
	private Long targetAmount;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Status status;

	public static GoalSummaryResponse from(Goal goal) {
		return GoalSummaryResponse.builder()
			.title(goal.getTitle())
			.targetAmount(goal.getTargetAmount())
			.startAt(goal.getStartAt())
			.endAt(goal.getEndAt())
			.status(goal.getStatus())
			.build();
	}

}
