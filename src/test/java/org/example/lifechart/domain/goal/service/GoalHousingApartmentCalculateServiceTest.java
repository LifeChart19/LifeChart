package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.dto.request.GoalHousingCalculateRequest;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalHousingApartmentCalculateServiceTest {

	@Mock
	private CachedApartmentPriceService apartmentPriceService;

	@InjectMocks
	private GoalHousingApartmentCalculateService goalCalculateService;

	@Test
	@DisplayName("주거 목표의 종료 시점이 1년 이상 이후인 경우 미래 금액을 예측하여 반환한다.")
	void calculateTargetAmount_종료_시점이_1년_이상_이후이면_미래_시점의_주거목표_금액을_계산한다() {
		// given
		GoalHousingCalculateRequest housingDetail = GoalHousingCalculateRequest.builder()
			.startAt(LocalDateTime.of(2025,8,1,0,0))
			.endAt(LocalDateTime.of(2030,8,1,0,0))
			.region("서울")
			.subregion("서남권")
			.area(100L)
			.housingType(HousingType.APARTMENT)
			.build();
		int yearsLater = 5;
		long expectedPrice = 2_000_000_000L; // 임의 가격

		given(apartmentPriceService.getFuturePredictedPrice("서울", "서남권", 100L, yearsLater)).willReturn(expectedPrice);

		// when
		Long result = goalCalculateService.calculateTargetAmount(housingDetail);

		// then
		assertThat(result).isEqualTo(expectedPrice);
	}

	@Test
	@DisplayName("주거 목표의 종료 시점이 1년 미만인 경우 현재 금액을 반환한다.")
	void calculateTargetAmount_종료_시점이_1년_미만의_미래_시점이면_현재기준_금액을_계산한다() {
		// given
		GoalHousingCalculateRequest housingDetail = GoalHousingCalculateRequest.builder()
			.startAt(LocalDateTime.of(2025,8,1,0,0))
			.endAt(LocalDateTime.of(2025,10,31,0,0))
			.region("서울")
			.subregion("서남권")
			.area(100L)
			.housingType(HousingType.APARTMENT)
			.build();

		Long expectedPrice = 1_400_000_000L;

		given(apartmentPriceService.getAveragePrice("서울", "서남권", 100L)).willReturn(expectedPrice);

		// when
		Long result = goalCalculateService.calculateTargetAmount(housingDetail);

		// then
		assertThat(result).isEqualTo(expectedPrice);
	}

	@Test
	@DisplayName("housingType이 APARTMENT인 경우에만 true를 반환한다")
	void supports_housingType이_APARTMENT일_때만_true를_반환한다() {
		assertThat(goalCalculateService.supports(HousingType.APARTMENT)).isTrue();
		assertThat(goalCalculateService.supports(HousingType.VILA)).isFalse();
	}
}
