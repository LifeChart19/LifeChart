// package org.example.lifechart.domain.goal.service;
//
// import java.time.LocalDate;
// import java.time.temporal.ChronoUnit;
//
// import org.example.lifechart.common.enums.ErrorCode;
// import org.example.lifechart.common.exception.CustomException;
// import org.example.lifechart.domain.goal.dto.request.GoalCalculateRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalDetailRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalEtcDetailRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalHousingDetailRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalRetirementDetailRequestDto;
// import org.example.lifechart.domain.goal.enums.Category;
// import org.example.lifechart.domain.goal.enums.HousingType;
// import org.example.lifechart.domain.goal.helper.GoalDateHelper;
// import org.example.lifechart.domain.goal.helper.GoalFallbackHelper;
// import org.example.lifechart.domain.user.entity.User;
// import org.springframework.stereotype.Service;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class GoalCalculateService implements GoalCalculator{
//
// 	private final ApartmentPriceServiceImpl apartmentPriceService;
// 	private final GoalFallbackHelper goalFallbackHelper;
//
// 	@Override
// 	public Long calculateTargetAmount(GoalCalculateRequestDto requestDto, User user) {
// 		GoalDetailRequestDto fallbackDetail = goalFallbackHelper.applyFallback(requestDto, user);
// 		return doCalculate(requestDto.getCategory(), requestDto.getEndAt().toLocalDate(), fallbackDetail, user);
// 	}
//
// 	@Override
// 	public Long calculateTargetAmount(GoalCreateRequestDto requestDto, User user) {
// 		GoalDetailRequestDto fallbackDetail = goalFallbackHelper.applyFallback(requestDto, user);
// 		return doCalculate(requestDto.getCategory(), requestDto.getEndAt().toLocalDate(), fallbackDetail, user);
// 	}
//
// 	private Long doCalculate(Category category, LocalDate endAt, GoalDetailRequestDto detail, User user) {
// 		switch (category) {
// 			case HOUSING -> {
// 				var housing = (GoalHousingDetailRequestDto)detail;
// 				return calculateHousingPrice(endAt, housing);
// 			}
// 			case RETIREMENT -> {
// 				var retirement = (GoalRetirementDetailRequestDto)detail;
// 				int birthYear = user.getBirthDate().getYear();
// 				return calculateRetirementPrice(endAt, retirement, birthYear);
// 			}
// 			case ETC -> {
// 				var etc = (GoalEtcDetailRequestDto)detail;
// 				return calculateEtcPrice(endAt, etc);
// 			}
// 			default -> throw new CustomException(ErrorCode.GOAL_INVALID_CATEGORY);
// 		}
// 	}
//
// 	@Override
// 	public float calculateProgressRate(Long asset, Long targetAmount) { // 입력 필드는 user의 자산, 목표 가격이 필요하겠다.
// 		if (asset == null || targetAmount == null || targetAmount ==0) {
// 			throw new IllegalArgumentException("자산 또는 목표 금액은 null이 아니고, 0보다 커야 합니다.");
// 		}
// 		float progressRate = (float) asset / targetAmount * 100; // 로직: 유저 현재 자산 / 목표 금액. %로 표현
// 		return Math.round(progressRate * 10 ) /10f; // 소수점 1자리 반올림
// 	}
//
// 	// 집값 계산 로직
// 	private Long calculateHousingPrice(LocalDate endAt, GoalHousingDetailRequestDto housing) {
// 		String region = housing.getRegion();
// 		String subregion = housing.getSubregion();
// 		Long area = housing.getArea();
// 		HousingType housingType = housing.getHousingType();
//
// 		// 이 정보로 DB에서 조회하는 로직 처리. endAt 활용하기
// 		Long calculatedPrice = apartmentPriceService.getAveragePrice(
// 			housing.getRegion(), housing.getSubregion(), housing.getArea());
// 		// Float avgInflation = 외부 데이터 가져오기
//
// 		// 값 계산 후 Long 값 반환
// 		return calculatedPrice;
// 	}
//
// 	// 은퇴비 계산 로직
// 	private Long calculateRetirementPrice(LocalDate endAt, GoalRetirementDetailRequestDto retirement, int birthYear) {
//
// 		// Float avgInflation = 외부 데이터 가져오기
// 		// 갖고 있는 데이터: 월 지출(monthlyExpense), 은퇴 타입(retirementType), 기대 수명(expectedLifespan), 은퇴시점(endAt)
// 		// 예상 사망일 - 은퇴 시점(LocalDate)을 월로 환산 > 월 지출(MonthlyExpense) * 기간(월) = targetAmount
// 		LocalDate expectedDeathDate = GoalDateHelper.toExpectedDeathDate(retirement.getExpectedLifespan(), birthYear);
// 		Long monthCount = ChronoUnit.MONTHS.between(endAt, expectedDeathDate);
// 		Long targetAmount = monthCount * retirement.getMonthlyExpense();
//
// 		// 인플레이션 로직을 추가할 수도 있음
//
// 		// 값 계산 후 Long 값 반환
// 		return targetAmount;
// 	}
//
// 	// etc 계산 로직
// 	private Long calculateEtcPrice(LocalDate endAt, GoalEtcDetailRequestDto etc) {
// 		Long expectedPrice = etc.getExpectedPrice();
// 		// Float avgInflation = 외부 데이터 가져오기
// 		// 이 정보로 DB에서 조회하는 로직 처리
//
// 		Long calculatedPrice = Math.round(expectedPrice * 1.02); // 로직 필요. endAt 활용하기
// 		// 값 계산 후 Long 값 반환
// 		return calculatedPrice;
// 	}
//
// }
