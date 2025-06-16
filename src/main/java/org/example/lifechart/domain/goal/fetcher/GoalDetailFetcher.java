package org.example.lifechart.domain.goal.fetcher;

import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.enums.Category;

public interface GoalDetailFetcher {
	boolean supports(Category category);
	GoalDetailInfoResponse fetch(Long goalId);
}
