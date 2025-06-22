package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class OpenApiApartmentPriceServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private OpenApiApartmentPriceService service;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(service, "apiKey", "mock-api-key");
		ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
	}

	@Test
	@DisplayName("최근 아파트 시세를 정상적으로 파싱한다.")
	void fetchLatest_해당_지역의_최근_아파트_시세를_정상적으로_파싱한다() {
		// given
		String region = "서울";
		String subregion = "동남권";

		String dummyJson = "[{\"C1\":\"0303\", \"C1_NM\":\"동남권\", \"PRD_DE\":\"202505\", \"DT\":\"1639.3\", \"UNIT_NM\":\"만원/m^2\"}]";

		given(restTemplate.getForObject(anyString(), eq(String.class))).willReturn(dummyJson);

		// when
		ApartmentPriceDto result = service.fetchLatest(region, subregion);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getRegion()).isEqualTo("서울");
		assertThat(result.getSubregion()).isEqualTo("동남권");
		assertThat(result.getPeriod()).isEqualTo("202505");
		assertThat(result.getPrice()).isEqualTo(1639.3);
		assertThat(result.getUnit()).isEqualTo("만원/m^2");
	}
}
