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
	private JsonApartmentPriceService apartmentPriceService;

	@InjectMocks
	private GoalHousingApartmentCalculateService goalCalculateService;

	@Test
	@DisplayName("주거 목표 금액 계산이 올바르게 수행된다")
	void 주거목표_금액이_올바르게_계산된다() {
		// given
		GoalHousingCalculateRequest housingDetail = GoalHousingCalculateRequest.builder()
			.region("서울")
			.subregion("서남권")
			.area(100L)
			.housingType(HousingType.APARTMENT)
			.endAt(LocalDateTime.now())
			.build();

		// Mocking
		when(apartmentPriceService.getAveragePrice("서울", "서남권", 100L)).thenReturn(143_210L);

		// when
		Long result = goalCalculateService.calculateTargetAmount(housingDetail);

		// then
		Long expected = Math.round(1432.1 * housingDetail.getArea());
		assertThat(result).isEqualTo(expected);
	}
}
