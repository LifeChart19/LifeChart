package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.example.lifechart.domain.goal.repository.ApartmentPriceCacheRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CachedApartmentPriceServiceTest {

	@Mock
	private ApartmentPriceCacheRepository redisRepository;

	@Mock
	private OpenApiApartmentPriceService openApiService;

	@InjectMocks
	private CachedApartmentPriceService service;

	@Test
	@DisplayName("캐시에 데이터가 있는 경우, OpenAPI를 호출하지 않고 가격을 반환한다.")
	void getAveragePrice_캐시에_있으면_OpenAPI를_호출하지_않는다() {
		// given
		String region = "서울";
		String subregion = "서남권";
		Long area = 100L;
		ApartmentPriceDto cachedDto = ApartmentPriceDto.builder()
			.region(region)
			.subregion(subregion)
			.price(1500.0)
			.unit("만원/m^2")
			.period("202505")
			.build();

		given(redisRepository.find(region,subregion)).willReturn(Optional.of(cachedDto));

		// when
		Long result = service.getAveragePrice(region, subregion, area);

		// then
		verify(redisRepository).find(region,subregion);
		assertThat(result).isEqualTo(1500L * 100L * 10_000L);
	}

	@Test
	@DisplayName("캐시에 데이터가 없으면 OpenAPI를 호출해 데이터를 저장하고, 가격을 반환한다.")
	void getAveragePrice_캐시에_데이터가_없으면_OpenAPI를_호출하고_데이터를_저장한뒤_가격을_반환한다() {
		// given
		String region = "서울";
		String subregion = "동남권";
		Long area = 50L;
		ApartmentPriceDto apiDto = ApartmentPriceDto.builder()
			.region(region)
			.subregion(subregion)
			.price(2000.0)
			.unit("만원/m^2")
			.period("202505")
			.build();

		given(redisRepository.find(region, subregion)).willReturn(Optional.empty());
		given(openApiService.fetchLatest(region, subregion)).willReturn(apiDto);

		// when
		Long result = service.getAveragePrice(region, subregion, area);

		// then
		verify(redisRepository).find(region, subregion);
		assertThat(result).isEqualTo(2000L * 50L * 10_000L);
	}

	@Test
	@DisplayName("미래 가격 예측은 현재가에 복리 공식을 적용한다.")
	void getFuturePredictedPrice_현재_가격에_복리_계산한_결과를_반환한다() {
		// given
		String region = "서울";
		String subregion = "도심";
		Long area = 100L;
		int yearsLater = 10;
		double rate = 0.03; // 연 평균 CAGR 3%
		ApartmentPriceDto dto = ApartmentPriceDto.builder()
			.region(region)
			.subregion(subregion)
			.price(2_000L)
			.unit("만원/m^2")
			.period("202505")
			.build();

		given(redisRepository.find(region, subregion)).willReturn(Optional.of(dto));
		given(openApiService.getAnnualGrowthRate(subregion)).willReturn(rate);

		// when
		Long result = service.getFuturePredictedPrice(region, subregion, area, yearsLater);

		// then
		Long expected = Math.round(2_000L * 100L * 10_000L *Math.pow(1+rate, yearsLater));

		verify(redisRepository).find(region, subregion);
		assertThat(result).isEqualTo(expected);
	}
}
