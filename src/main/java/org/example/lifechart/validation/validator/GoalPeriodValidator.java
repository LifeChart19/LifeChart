package org.example.lifechart.validation.validator;

import java.time.LocalDateTime;

import org.example.lifechart.validation.annotation.ValidGoalPeriod;
import org.example.lifechart.validation.support.HaSGoalPeriod;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GoalPeriodValidator implements ConstraintValidator<ValidGoalPeriod, HaSGoalPeriod> {

	@Override
	public boolean isValid(HaSGoalPeriod dto, ConstraintValidatorContext context) {
		if (dto == null) return true;

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startAt = dto.getStartAt() != null ? dto.getStartAt() : now;
		LocalDateTime endAt = dto.getEndAt();

		if (endAt != null && !endAt.isAfter(startAt)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("종료일은 시작일 이후여야 합니다.")
				.addPropertyNode("endAt")
				.addConstraintViolation();
			return false;
		}

		if (dto.getStartAt() != null && dto.getStartAt().isBefore(now)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("시작일은 현재 시점 이후여야 합니다.")
				.addPropertyNode("startAt")
				.addConstraintViolation();
			return false;
		}

		return true;
	}
}
