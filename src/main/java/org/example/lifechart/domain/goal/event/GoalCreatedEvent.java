package org.example.lifechart.domain.goal.event;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GoalCreatedEvent {
	private final Long userId;
	private final Long goalId;
	private final List<Long> simulationIds;
}
