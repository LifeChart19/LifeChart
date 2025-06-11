package org.example.lifechart.domain.goal.helper;

import org.example.lifechart.domain.goal.dto.request.GoalCalculateRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalEtcDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalHousingDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementDetailRequestDto;
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
			case RETIREMENT -> ((GoalRetirementDetailRequestDto) detail)
				.withFallbacks(user, standardValueService);
			case HOUSING -> (GoalHousingDetailRequestDto) detail; // 모든 값 필수 입력
			case ETC -> (GoalEtcDetailRequestDto) detail; // 모든 값 필수 입력
		};
	}

	public GoalDetailRequestDto applyFallback(GoalCalculateRequestDto dto, User user) {
		Category category = dto.getCategory();
		GoalDetailRequestDto detail = dto.getDetail();

		return switch (category) {
			case RETIREMENT -> ((GoalRetirementDetailRequestDto) detail)
				.withFallbacks(user, standardValueService);
			case HOUSING -> (GoalHousingDetailRequestDto) detail;
			case ETC -> (GoalEtcDetailRequestDto) detail;
		};
	}
}
