package org.example.lifechart.domain.goal.fetcher;

import java.util.List;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalDetailFetcherFactory {

	private final List<GoalDetailFetcher> fetchers;

	public GoalDetailInfoResponse getDetail(Goal goal) {
		return fetchers.stream()
			.filter(f -> f.supports(goal.getCategory()))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.GOAL_INVALID_CATEGORY))
			.fetch(goal.getId());
	}
}
