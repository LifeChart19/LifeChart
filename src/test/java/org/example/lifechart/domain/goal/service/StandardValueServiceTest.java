// package org.example.lifechart.domain.goal.service;
//
// import static org.assertj.core.api.AssertionsForClassTypes.*;
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.util.Map;
//
// import org.example.lifechart.common.enums.ErrorCode;
// import org.example.lifechart.common.exception.CustomException;
// import org.example.lifechart.support.TestJsonLoader;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
//
// import com.fasterxml.jackson.core.type.TypeReference;
//
// public class StandardValueServiceTest {
//
// 	private StandardValueService standardValueService;
//
// 	@BeforeEach
// 	void Setup() {
// 		Map<String, Map<String, Double>> testData =
// 			TestJsonLoader.loadJson("/data/lifespan_data.json", new TypeReference<>() {});
// 		standardValueService = new StandardValueService(testData);
// 	}
//
// 	@Test
// 	@DisplayName("기대 수명을 정상적으로 반환한다.")
// 	void 은퇴_기대_수명을_정상적으로_반환한다() {
// 		// given
// 		String gender = "male";
// 		int year = 2024;
//
// 		// when
// 		Long expectedAge = standardValueService.getExpectedLifespan(gender, year);
//
// 		// then
// 		assertThat(expectedAge).isEqualTo(Math.round(81.4));
// 	}
//
// 	@Test
// 	@DisplayName("성별 입력이 잘못되면 남녀 평균 기대 수명 데이터를 반환한다.")
// 	void 은퇴_유저의_성별이_Null이면_평균_기대수명_데이터를_반환한다() {
// 		// given
// 		String gender = "남자";
// 		int year = 2025;
//
// 		// when
// 		Long expectedAge = standardValueService.getExpectedLifespan(gender, 2025);
//
// 		// then
// 		assertThat(expectedAge).isEqualTo(Math.round(85.0));
// 	}
//
// 	@Test
// 	@DisplayName("입력한 연도가 데이터에 없으면 에러코드를 반환한다.")
// 	void 은퇴_입력한_연도가_데이터에_없으면_GOAL_LIFESPAN_DATA_NOT_EXIST_에러코드를_반환한다() {
// 		// given
// 		String gender = "남자";
// 		int year = 2100;
//
// 		// when
// 		CustomException customException = assertThrows(CustomException.class, () ->
// 			standardValueService.getExpectedLifespan(null, year));
//
// 		// then
// 		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_LIFESPAN_DATA_NOT_EXIST);
// 	}
// }
