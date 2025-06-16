package org.example.lifechart.domain.goal.fetcher;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalEtcInfoResponse;
import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.repository.GoalEtcRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoalEtcFetcher implements GoalDetailFetcher{

	private final GoalEtcRepository goalEtcRepository;

	@Override
	public boolean supports(Category category) {
		return (category == Category.ETC);
	}

	@Override
	public GoalDetailInfoResponse fetch(Long goalId) {
		GoalEtc goalEtc = goalEtcRepository.findByGoalId(goalId)
			.orElseThrow(() -> new CustomException(ErrorCode.GOAL_ETC_NOT_FOUND));
		return GoalEtcInfoResponse.from(goalEtc);
	}
}
