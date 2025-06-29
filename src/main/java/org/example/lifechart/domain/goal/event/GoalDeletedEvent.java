package org.example.lifechart.domain.goal.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GoalDeletedEvent {

	private final Long goalId;
}
