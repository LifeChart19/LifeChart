package org.example.lifechart.domain.goal.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementCalculateRequest;
import org.example.lifechart.domain.goal.helper.GoalDateHelper;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalRetirementCalculateService{

	private final UserRepository userRepository;

	public Long calculateTargetAmount(GoalRetirementCalculateRequest request, Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

		int currentAge = LocalDate.now().getYear() - user.getBirthDate().getYear();

		if (request.getExpectedLifespan() <= currentAge) {
			throw new CustomException(ErrorCode.INVALID_EXPECTED_LIFESPAN);
		}

		LocalDate endAt = request.getEndAt().toLocalDate();
		LocalDate expectedDeathDate = GoalDateHelper.toExpectedDeathDate(request.getExpectedLifespan(), user.getBirthDate().getYear());

		if (endAt.isAfter(expectedDeathDate)) {
			throw new CustomException(ErrorCode.GOAL_RETIREMENT_LIFESPAN_BEFORE_END_DATE);
		}

		Long monthsBetween = ChronoUnit.MONTHS.between(endAt, expectedDeathDate);

		return monthsBetween * request.getMonthlyExpense();
	}
}