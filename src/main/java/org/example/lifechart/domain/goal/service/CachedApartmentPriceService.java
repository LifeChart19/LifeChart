package org.example.lifechart.domain.goal.service;

import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.example.lifechart.domain.goal.repository.ApartmentPriceCacheRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CachedApartmentPriceService implements ApartmentPriceService {

	private final ApartmentPriceCacheRepository redisRepository;
	private final OpenApiApartmentPriceService openApiService;

	@Override
	public Long getAveragePrice(String region, String subregion, Long area) {
		ApartmentPriceDto dto = redisRepository.find(region, subregion)
			.orElseGet(() -> {
				ApartmentPriceDto fetched = openApiService.fetchLatest(region, subregion);
				redisRepository.save(fetched);
				return fetched;
			});
		return Math.round(dto.getPrice() * area * 10_000L); // price 단위가 만원으로, 원 단위 환산
	}

	@Override // 추후 목표 설정의 '종료일'과 결합해 활용할 예정
	public Long getFuturePredictedPrice(String region, String subregion, Long area, int yearsLater) {
		Long current = getAveragePrice(region, subregion, area);
		double rate = openApiService.getAnnualGrowthRate(subregion);
		return Math.round(current * Math.pow(1+rate, yearsLater));
	}

}
