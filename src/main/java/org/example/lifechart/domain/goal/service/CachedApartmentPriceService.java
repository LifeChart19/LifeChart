package org.example.lifechart.domain.goal.service;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.example.lifechart.domain.goal.repository.ApartmentPriceCacheRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CachedApartmentPriceService implements ApartmentPriceService {

	private final ApartmentPriceCacheRepository redisRepository;
	private final OpenApiApartmentPriceService openApiService;

	@Override // 현재 시점의 가격 계싼
	public Long getAveragePrice(String region, String subregion, Long area) {
		ApartmentPriceDto dto = redisRepository.find(region, subregion)
			.orElseGet(() -> {
				ApartmentPriceDto fetched = openApiService.fetchLatest(region, subregion);
				redisRepository.save(fetched);
				return fetched;
			});
		return Math.round(dto.getPrice() * area * 10_000L); // price 단위가 만원으로, 원 단위 환산
	}

	@Override // 미래 시점의 가격 계싼
	public Long getFuturePredictedPrice(String region, String subregion, Long area, int yearsLater) {
		Long current = getAveragePrice(region, subregion, area);
		double rate = calculateAnnualGrowthRate(region, subregion, 10);
		return Math.round(current * Math.pow(1+rate, yearsLater));
	}

	private double calculateAnnualGrowthRate(String region, String subregion, int years) {
		// 1. 과거 N년 데이터 중 가장 최신/오래된 데이터 가져오기
		Optional<Pair<ApartmentPriceDto, ApartmentPriceDto>> pairOpt = redisRepository.findStartAndEnd(region, subregion, years);

		if (pairOpt.isEmpty()) return 0.03; // fallback

		// 2. 가장 오래된 값과 최신값 비교
		ApartmentPriceDto start = pairOpt.get().getLeft();
		ApartmentPriceDto end = pairOpt.get().getRight();

		// 3. CAGR (복리 성장률) 계산
		return Math.pow(end.getPrice() / start.getPrice(), 1.0/ years) - 1 ;
	}

}
