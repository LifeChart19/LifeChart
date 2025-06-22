package org.example.lifechart.domain.goal.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.example.lifechart.domain.goal.dto.request.GoalHousingCalculateRequest;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalHousingApartmentCalculateService implements GoalHousingCalculateService {

	private final CachedApartmentPriceService apartmentPriceService;

	@Override
	public boolean supports(HousingType type) {
		return type == HousingType.APARTMENT;
	};

	@Override
	public Long calculateTargetAmount(GoalHousingCalculateRequest request) {

		String region = request.getRegion();
		String subregion = request.getSubregion();
		Long area = request.getArea();
		LocalDateTime startAt = request.getStartAt();
		LocalDateTime endAt = request.getEndAt();
		int yearsLater = (int) ChronoUnit.YEARS.between(startAt, endAt);

		if (isFuturePeriod(startAt, endAt)) {
			return apartmentPriceService.getFuturePredictedPrice(region, subregion, area, yearsLater);
		} else {
			return apartmentPriceService.getAveragePrice(region, subregion, area);
		}
	}

	private boolean isFuturePeriod(LocalDateTime startAt, LocalDateTime endAt) {
		return endAt.isAfter(startAt.plusMonths(12)); // 12개월 이상이면 미래로 간주
	}
}
