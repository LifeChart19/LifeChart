package org.example.lifechart.domain.goal.fetcher;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalHousingInfoResponse;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.repository.GoalHousingRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoalHousingFetcher implements GoalDetailFetcher{

	private final GoalHousingRepository goalHousingRepository;

	@Override
	public boolean supports(Category category) {
		return category == Category.HOUSING;
	}

	@Override
	public GoalDetailInfoResponse fetch(Long goalId) {
		GoalHousing goalHousing = goalHousingRepository.findByGoalId(goalId)
			.orElseThrow(()-> new CustomException(ErrorCode.GOAL_HOUSING_NOT_FOUND));

		return GoalHousingInfoResponse.from(goalHousing);
	}
}
