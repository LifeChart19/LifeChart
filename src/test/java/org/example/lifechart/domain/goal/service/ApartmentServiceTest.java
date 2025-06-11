// package org.example.lifechart.domain.goal.service;
//
// import static org.assertj.core.api.AssertionsForClassTypes.*;
//
// import java.util.Map;
//
// import org.example.lifechart.support.TestJsonLoader;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
//
// import com.fasterxml.jackson.core.type.TypeReference;
//
// public class ApartmentServiceTest {
//
// 	private ApartmentPriceServiceImpl apartmentPriceService;
//
// 	@BeforeEach
// 	void setUp() {
// 		Map<String, Map<String, Double>> testData =
// 			TestJsonLoader.loadJson("data/apartment_price.json", new TypeReference<>() {});
// 		apartmentPriceService = new ApartmentPriceServiceImpl(testData);
// 	}
//
// 	@Test
// 	@DisplayName("평균 가격을 정상적으로 계산한다.")
// 	void 아파트_평균_가격을_정상적으로_계산한다() {
// 		// given
// 		String region = "서울";
// 		String subregion ="서남권";
// 		Long area = 84L; // 100m^2 아파트
//
// 		// when
// 		Long price = apartmentPriceService.getAveragePrice(region, subregion, area);
//
// 		// then
// 		assertThat(price).isEqualTo(Math.round(1432.1*84));
// 	}
//
// 	@Test
// 	@DisplayName("지역을 잘못 선택하면 0을 반환한다.")
// 	void 아파트_지역을_잘못_선택하면_0을_반환() {
// 		// given
// 		String region = "뉴옥";
// 		String subregion = "서남권";
// 		Long area = 100L ;
//
// 		// when
// 		Long price = apartmentPriceService.getAveragePrice(region, subregion, area);
//
// 		// then
// 		assertThat(price).isEqualTo(0);
// 	}
//
// 	@Test
// 	@DisplayName("지역을 잘못 선택하면 0을 반환한다.")
// 	void 아파트_세부_지역을_잘못_선택하면_0을_반환() {
// 		// given
// 		String region = "서울";
// 		String subregion = "강남구";
// 		Long area = 100L ;
//
// 		// when
// 		Long price = apartmentPriceService.getAveragePrice(region, subregion, area);
//
// 		// then
// 		assertThat(price).isEqualTo(0);
// 	}
// }
