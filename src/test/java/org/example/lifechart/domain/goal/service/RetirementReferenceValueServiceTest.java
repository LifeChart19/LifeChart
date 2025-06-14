package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Map;
import java.util.Optional;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalRetirementEstimateResponse;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.support.TestJsonLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;

@ExtendWith(MockitoExtension.class)
public class RetirementReferenceValueServiceTest {

	@Mock
	private UserRepository userRepository;

	private RetirementReferenceValueService retirementReferenceValueService;

	@BeforeEach
	void setup() {
		Map<String, Map<String, Double>> testData =
			TestJsonLoader.loadJson("/data/lifespan_data.json", new TypeReference<>() {});
		retirementReferenceValueService = new RetirementReferenceValueService(testData, userRepository);
	}

	@Test
	@DisplayName("기대 수명을 정상적으로 반환한다.")
	void 은퇴_기대수명_월지출_은퇴타입을_정상적으로_반환한다() {
		// given
		User user = User.builder()
			.id(1L)
			.gender("male")
			.build();

		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
		int currentYear = 2025;

		// when
		GoalRetirementEstimateResponse response = retirementReferenceValueService.getReferenceValues(user.getId(), currentYear);

		// then
		verify(userRepository).findById(user.getId());
		assertThat(response.getExpectedLifespan()).isEqualTo(Math.round(82.1));
		assertThat(response.getMonthlyExpense()).isEqualTo(2_793_000L);
		assertThat(response.getRetirementType()).isEqualTo(RetirementType.COUPLE);
	}

	@Test
	@DisplayName("성별 입력이 잘못되면 남녀 평균 기대 수명 데이터를 반환한다.")
	void 은퇴_유저의_성별입력이_잘못된_값이면_전체_평균_기대수명_데이터를_반환한다() {
		// given
		User user = User.builder()
			.id(1L)
			.gender("중성")
			.build();

		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

		int currentYear = 2025;

		// when
		GoalRetirementEstimateResponse response = retirementReferenceValueService.getReferenceValues(user.getId(), currentYear);

		// then
		verify(userRepository).findById(user.getId());
		assertThat(response.getExpectedLifespan()).isEqualTo(Math.round(85.0));
	}

	@Test
	@DisplayName("성별 입력값이 DB에 없고, 해당 연도에 전체 평균 기대수명 데이터가 없으면 GOAL_LIFESPAN_DATA_NOT_EXIST 에러코드를 반환한다.")
	void 전체_평균_기대수명_데이터가_없으면_에러코드를_반환한다() {
		// given
		User user = User.builder()
			.id(1L)
			.gender("중성")
			.build();

		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

		int currentYear = 2030;

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			retirementReferenceValueService.getReferenceValues(user.getId(), currentYear));

		// then
		verify(userRepository).findById(user.getId());
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_LIFESPAN_DATA_NOT_EXIST);
	}

	@Test
	@DisplayName("입력한 연도가 데이터에 없으면 GOAL_LIFESPAN_DATA_NOT_EXIST_에러코드를 반환한다.")
	void 은퇴_입력한_연도가_데이터에_없으면_에러코드를_반환한다() {
		// given
		User user = User.builder()
			.id(1L)
			.gender("남자")
			.build();

		int currentYear = 2100;

		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

		// when
		CustomException customException = assertThrows(CustomException.class, () ->
			retirementReferenceValueService.getReferenceValues(user.getId(), currentYear));

		// then
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.GOAL_LIFESPAN_DATA_NOT_EXIST);
	}
}
