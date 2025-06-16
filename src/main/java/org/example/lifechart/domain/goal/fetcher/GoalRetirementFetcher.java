package org.example.lifechart.domain.goal.fetcher;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalRetirementInfoResponse;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.repository.GoalRetirementRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoalRetirementFetcher implements GoalDetailFetcher{

	private final GoalRetirementRepository goalRetirementRepository;

	@Override
	public boolean supports(Category category) {
		return (category == Category.RETIREMENT);
	}

	@Override
	public GoalDetailInfoResponse fetch(Long goalId) {
		GoalRetirement goalRetirement = goalRetirementRepository.findByGoalId(goalId)
			.orElseThrow(()-> new CustomException(ErrorCode.GOAL_RETIREMENT_NOT_FOUND));

		return GoalRetirementInfoResponse.from(goalRetirement);
	}
}
