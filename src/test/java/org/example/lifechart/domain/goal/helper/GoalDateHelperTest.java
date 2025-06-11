// package org.example.lifechart.domain.goal.helper;
//
// import static org.assertj.core.api.AssertionsForClassTypes.*;
//
// import java.time.LocalDate;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
//
// public class GoalDateHelperTest {
//
// 	@Test
// 	@DisplayName("기대 수명과 생년을 입력하면 예상 사망 연도를 정상적으로 반환한다.")
// 	void 은퇴_예상_사망_연도를_정상적으로_반환한다() {
// 		// given
// 		Long lifespan = 90L;
// 		int birthYear = 1993;
//
// 		// when
// 		LocalDate expectedDeathDate = GoalDateHelper.toExpectedDeathDate(lifespan, birthYear);
//
// 		// then
// 		assertThat(expectedDeathDate).isEqualTo(LocalDate.of(2083,12,31));
// 	}
// }
