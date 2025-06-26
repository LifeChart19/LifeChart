package org.example.lifechart.domain.goal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementCalculateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequest;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.dto.response.GoalRetirementEstimateResponse;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.goal.enums.Share;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultRetirementGoalService {

	private final GoalService goalService;
	private final RetirementReferenceValueService retirementReferenceValueService;
	private final GoalRetirementCalculateService retirementCalculateService;

	private static final String DEFAULT_TITLE = "기본 설정 은퇴 목표";
	private static final List<String> DEFAULT_TAGS = List.of("은퇴");

	@Transactional
	public GoalResponse createDefaultRetirementGoal(Long userId) {

		LocalDateTime startAt = LocalDateTime.now();

		GoalRetirementEstimateResponse retirementResponse = retirementReferenceValueService.getReferenceValues(userId, startAt.getYear());

		GoalRetirementCalculateRequest calculateRequest = GoalRetirementCalculateRequest.builder()
			.startAt(startAt)
			.endAt(retirementResponse.getExpectedRetirementDate())
			.expectedLifespan(retirementResponse.getExpectedLifespan())
			.monthlyExpense(retirementResponse.getMonthlyExpense())
			.retirementType(retirementResponse.getRetirementType())
			.build();

		Long targetAmount = retirementCalculateService.calculateTargetAmount(calculateRequest, userId);

		GoalRetirementRequest retirementRequest = GoalRetirementRequest.builder()
			.monthlyExpense(retirementResponse.getMonthlyExpense())
			.retirementType(retirementResponse.getRetirementType())
			.expectedLifespan(retirementResponse.getExpectedLifespan())
			.build();

		GoalCreateRequest createRequest = GoalCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(Category.RETIREMENT)
			.startAt(startAt)
			.share(Share.PRIVATE)
			.tags(DEFAULT_TAGS)
			.endAt(retirementResponse.getExpectedRetirementDate())
			.detail(retirementRequest)
			.targetAmount(targetAmount)
			.build();

		return goalService.createGoal(createRequest, userId);
	}
}
