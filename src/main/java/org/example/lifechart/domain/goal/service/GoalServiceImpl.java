// package org.example.lifechart.domain.goal.service;
//
// import java.time.LocalDate;
// import java.util.Locale;
//
// import org.example.lifechart.common.enums.ErrorCode;
// import org.example.lifechart.common.exception.CustomException;
// import org.example.lifechart.domain.goal.dto.request.GoalCreateRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalDetailRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalEtcDetailRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalHousingDetailRequestDto;
// import org.example.lifechart.domain.goal.dto.request.GoalRetirementDetailRequestDto;
// import org.example.lifechart.domain.goal.dto.response.GoalResponseDto;
// import org.example.lifechart.domain.goal.entity.Goal;
// import org.example.lifechart.domain.goal.entity.GoalEtc;
// import org.example.lifechart.domain.goal.entity.GoalHousing;
// import org.example.lifechart.domain.goal.entity.GoalRetirement;
// import org.example.lifechart.domain.goal.enums.Category;
// import org.example.lifechart.domain.goal.helper.GoalDateHelper;
// import org.example.lifechart.domain.goal.helper.GoalFallbackHelper;
// import org.example.lifechart.domain.goal.repository.GoalEtcRepository;
// import org.example.lifechart.domain.goal.repository.GoalHousingRepository;
// import org.example.lifechart.domain.goal.repository.GoalRepository;
// import org.example.lifechart.domain.goal.repository.GoalRetirementRepository;
// import org.example.lifechart.domain.user.entity.User;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Service
// @Slf4j
// @RequiredArgsConstructor
// public class GoalServiceImpl {
//
// 	private final GoalRepository goalRepository;
// 	private final GoalCalculateService goalCalculateService;
// 	private final GoalRetirementRepository goalRetirementRepository;
// 	private final GoalHousingRepository goalHousingRepository;
// 	private final GoalEtcRepository goalEtcRepository;
// 	private final GoalFallbackHelper goalFallbackHelper;
//
// 	@Transactional
// 	public GoalResponseDto createGoal(GoalCreateRequestDto requestDto, User user) {
// 		// 1. fallback 적용 (null 값 보정)
// 		GoalDetailRequestDto fallbackAppliedDetail = goalFallbackHelper.applyFallback(requestDto, user);
//
// 		// 1.5 기대 수명 유효성 검증 (RETIREMENT 카테고리일 경우) ... 애도 나중에 메서드 분리할 수 있을 것 같음
// 		if (requestDto.getCategory() == Category.RETIREMENT) {
// 			GoalRetirementDetailRequestDto retirementDetail = (GoalRetirementDetailRequestDto) fallbackAppliedDetail;
// 			LocalDate expectedDeathDate = GoalDateHelper.toExpectedDeathDate(
// 				retirementDetail.getExpectedLifespan(),
// 				user.getBirthDate().getYear()
// 			);
//
// 			if (expectedDeathDate.isBefore(requestDto.getEndAt().toLocalDate())) {
// 				throw new CustomException(ErrorCode.GOAL_RETIREMENT_LIFESPAN_BEFORE_END_DATE);
// 			}
// 		}
//
// 		// 2. 목표 금액 계산
// 		Long targetAmount = requestDto.getTargetAmount() != null
// 			? requestDto.getTargetAmount()
// 			: goalCalculateService.calculateTargetAmount(requestDto, user);
//
// 		// 3. progressRate 계산
// 		Long asset = 100_000_000L; // 더미 데이터. 추후 user에서 갖고 오기.
// 		float progressRate = goalCalculateService.calculateProgressRate(asset, targetAmount); // 유저 자산이 입력 필드로 필요할 듯
//
// 		// 4. Goal 저장
// 		Goal newGoal = requestDto.toEntity(user, targetAmount, progressRate);
// 		Goal savedGoal = goalRepository.save(newGoal);
//
// 		// 5. 세부 목표 저장
// 		if (fallbackAppliedDetail instanceof GoalRetirementDetailRequestDto retirementDetail) {
// 			GoalRetirement goalRetirement = retirementDetail.toEntity(savedGoal, user.getBirthDate().getYear());
// 			goalRetirementRepository.save(goalRetirement);
// 		} else if (fallbackAppliedDetail instanceof GoalHousingDetailRequestDto housingDetail) {
// 			GoalHousing goalHousing = housingDetail.toEntity(savedGoal);
// 			goalHousingRepository.save(goalHousing);
// 		} else if (fallbackAppliedDetail instanceof GoalEtcDetailRequestDto etcDetail) {
// 			GoalEtc goalEtc = etcDetail.toEntity(savedGoal);
// 			goalEtcRepository.save(goalEtc);
// 		} else {
// 			throw new CustomException(ErrorCode.GOAL_INVALID_CATEGORY);
// 		}
//
// 		return GoalResponseDto.from(savedGoal);
// 	}
// }
