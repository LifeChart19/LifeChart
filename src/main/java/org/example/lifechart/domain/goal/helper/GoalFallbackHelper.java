package org.example.lifechart.domain.goal.helper;

import org.example.lifechart.domain.goal.dto.request.GoalCalculateRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalEtcRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalHousingRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequestDto;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.service.StandardValueService;
import org.example.lifechart.domain.user.entity.User;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoalFallbackHelper {
	private final StandardValueService standardValueService;

	public GoalDetailRequestDto applyFallback(GoalCreateRequestDto dto, User user) {
		Category category = dto.getCategory();
		GoalDetailRequestDto detail = dto.getDetail();

		return switch (category) {
			case RETIREMENT -> ((GoalRetirementRequestDto) detail)
				.withFallbacks(user, standardValueService);
			case HOUSING -> (GoalHousingRequestDto) detail; // 모든 값 필수 입력
			case ETC -> (GoalEtcRequestDto) detail; // 모든 값 필수 입력
		};
	}

	public GoalDetailRequestDto applyFallback(GoalCalculateRequestDto dto, User user) {
		Category category = dto.getCategory();
		GoalDetailRequestDto detail = dto.getDetail();

		return switch (category) {
			case RETIREMENT -> ((GoalRetirementRequestDto) detail)
				.withFallbacks(user, standardValueService);
			case HOUSING -> (GoalHousingRequestDto) detail;
			case ETC -> (GoalEtcRequestDto) detail;
		};
	}
}
