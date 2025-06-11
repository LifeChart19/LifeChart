package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.dto.request.GoalCalculateRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalEtcDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalHousingDetailRequestDto;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementDetailRequestDto;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.goal.helper.GoalFallbackHelper;
import org.example.lifechart.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class GoalCalculateServiceTest {

	@Mock
	private ApartmentPriceServiceImpl apartmentPriceService;

	@Mock
	private GoalFallbackHelper goalFallbackHelper;

	@InjectMocks
	private GoalCalculateService goalCalculateService;

	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.nickname("홍길동")
			.birthDate(LocalDate.of(1990, 1, 1))
			.gender("male")
			.build();
	}

	@Test
	@DisplayName("주거 목표 금액 계산이 올바르게 수행된다")
	void 주거목표_금액이_올바르게_계산된다() {
		// given
		GoalHousingDetailRequestDto housingDetail = GoalHousingDetailRequestDto.builder()
			.region("서울")
			.subregion("서남권")
			.area(100L)
			.housingType(HousingType.APARTMENT)
			.build();

		GoalCreateRequestDto requestDto = GoalCreateRequestDto.builder()
			.category(Category.HOUSING)
			.endAt(LocalDateTime.of(2030, 1, 1, 0, 0))
			.detail(housingDetail)
			.build();

		// Mocking fallback
		when(goalFallbackHelper.applyFallback(requestDto, user)).thenReturn(housingDetail);
		when(apartmentPriceService.getAveragePrice("서울", "서남권", 100L)).thenReturn(143_210L);

		// when
		Long result = goalCalculateService.calculateTargetAmount(requestDto, user);

		// then
		Long expected = Math.round(1432.1 * housingDetail.getArea());
		assertThat(result).isEqualTo(expected);
	}

	@Tag("retirement")
	@Test
	@DisplayName("은퇴 목표 금액 계산이 올바르게 수행된다")
	public void retirement_goal_calculate_success() {
		// given
		GoalRetirementDetailRequestDto retirementDetail = GoalRetirementDetailRequestDto.builder()
			.expectedLifespan(85L)
			.monthlyExpense(2000000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		GoalCalculateRequestDto requestDto = GoalCalculateRequestDto.builder()
			.category(Category.RETIREMENT)
			.endAt(LocalDateTime.of(2055,1, 31,0,0,0))
			.detail(retirementDetail)
			.build();

		when(goalFallbackHelper.applyFallback(requestDto, user)).thenReturn(retirementDetail);

		// when
		Long result = goalCalculateService.calculateTargetAmount(requestDto, user);

		// 예상 사망일: 1990 + 85 = 2075 -> 2055.01.31~2075.12.31 = 20년 * 251개월 * 2백만원 = 5.02억
		assertThat(result).isEqualTo(502_000_000L);
	}

	@Test
	@DisplayName("기타(ETC) 목표 금액 게산이 올바르게 수행된다")
	void 기타목표_금액이_올바르게_계산된다() {
		// given
		GoalEtcDetailRequestDto etcDetail = GoalEtcDetailRequestDto.builder()
			.expectedPrice(1_000_000L)
			.build();

		GoalCalculateRequestDto requestDto = GoalCalculateRequestDto.builder()
			.category(Category.ETC)
			.endAt(LocalDateTime.of(2027,1,1,0,0))
			.detail(etcDetail)
			.build();

		given(goalFallbackHelper.applyFallback(requestDto, user)).willReturn(etcDetail);

		// when
		Long calculated = goalCalculateService.calculateTargetAmount(requestDto, user);

		// then
		assertThat(calculated).isEqualTo(1_020_000L);

	}
}
