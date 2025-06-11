package org.example.lifechart.domain.goal.helper;

import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.example.lifechart.domain.goal.dto.request.GoalCalculateRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalEtcDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalHousingDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementDetailRequestDto;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.domain.goal.service.StandardValueService;
import org.example.lifechart.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalFallbackHelperTest {

	@Mock
	private StandardValueService standardValueService;

	@InjectMocks
	private GoalFallbackHelper goalFallbackHelper;

	private User user;



	/**
	 * GoalCreateRequestDto, GoalCalculateRequestDto
	 * category -> GOAL, HOUSING, RETIREMENT
	 * detail ->
	 * 1) retirementdetail
	 * 2) etcdetail
	 * 3) housingdetail
	 */

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.birthDate(LocalDate.of(2020,01,01))
			.gender("male")
			.build();

		GoalDetailRequestDto housingDto = GoalHousingDetailRequestDto.builder()
			.region("서울")
			.subregion("서남권")
			.area(100L)
			.housingType(HousingType.APARTMENT)
			.build();

		GoalDetailRequestDto retirementDto = GoalRetirementDetailRequestDto.builder().build();

		GoalDetailRequestDto etcDto = GoalEtcDetailRequestDto.builder()
			.build();

		GoalCalculateRequestDto calHousingDto = GoalCalculateRequestDto.builder()
			.category(Category.HOUSING)
			.detail(housingDto)
			.build();

		GoalCalculateRequestDto calRetirementDto = GoalCalculateRequestDto.builder()
			.category(Category.RETIREMENT)
			.detail(retirementDto)
			.build();

		GoalCalculateRequestDto calEtcDto = GoalCalculateRequestDto.builder()
			.category(Category.ETC)
			.detail(etcDto)
			.build();

		GoalCreateRequestDto createHousingDto = GoalCreateRequestDto.builder()
			.category(Category.HOUSING)
			.detail(housingDto)
			.build();

		GoalCreateRequestDto createRetirementDto = GoalCreateRequestDto.builder()
			.category(Category.RETIREMENT)
			.detail(retirementDto)
			.build();

		GoalCreateRequestDto createEtcDto = GoalCreateRequestDto.builder()
			.category(Category.ETC)
			.detail(etcDto)
			.build();
	}

	@Test
	@DisplayName("은퇴 목표 값을 입력하지 않으면 fallback값을 정상적으로 설정한다")
	void 은퇴_목표값을_입력하지_않을때_fallback값_정상_반환() {

		GoalRetirementDetailRequestDto retirementDetail = GoalRetirementDetailRequestDto.builder()
			.build();

		GoalCreateRequestDto createRetirementDto = GoalCreateRequestDto.builder()
			.category(Category.RETIREMENT)
			.detail(retirementDetail)
			.build();

		// given
		when(goalFallbackHelper.applyFallback(createRetirementDto, user).willReturn(retirementDetail)

		// when

		// then

	}

}
