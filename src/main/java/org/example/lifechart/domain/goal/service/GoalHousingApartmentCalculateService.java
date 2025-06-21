package org.example.lifechart.domain.goal.service;

import org.example.lifechart.domain.goal.dto.request.GoalHousingCalculateRequest;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalHousingApartmentCalculateService implements GoalHousingCalculateService {

	//private final JsonApartmentPriceService apartmentPriceService;
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
		return apartmentPriceService.getAveragePrice(region, subregion, area);
	}

	@Override
	public Long calculateFutureTargetAmount(GoalHousingCalculateRequest request, int yearsLater) {
		String region = request.getRegion();
		String subregion = request.getSubregion();
		Long area = request.getArea();
		return apartmentPriceService.getFuturePredictedPrice(region, subregion, area, yearsLater);
	}

	;
}
