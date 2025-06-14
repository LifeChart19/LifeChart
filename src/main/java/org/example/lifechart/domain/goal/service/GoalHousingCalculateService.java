package org.example.lifechart.domain.goal.service;

import org.example.lifechart.domain.goal.dto.request.GoalHousingCalculateRequest;
import org.example.lifechart.domain.goal.enums.HousingType;

public interface GoalHousingCalculateService {
	boolean supports(HousingType type);
	Long calculateTargetAmount(GoalHousingCalculateRequest request);
}
