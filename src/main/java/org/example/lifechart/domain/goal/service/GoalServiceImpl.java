package org.example.lifechart.domain.goal.service;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalDetailRequest;
import org.example.lifechart.domain.goal.dto.request.GoalEtcRequest;
import org.example.lifechart.domain.goal.dto.request.GoalHousingRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequest;
import org.example.lifechart.domain.goal.dto.response.GoalResponseDto;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.repository.GoalEtcRepository;
import org.example.lifechart.domain.goal.repository.GoalHousingRepository;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.goal.repository.GoalRetirementRepository;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalServiceImpl {

	private final GoalRepository goalRepository;
	private final GoalRetirementRepository goalRetirementRepository;
	private final GoalHousingRepository goalHousingRepository;
	private final GoalEtcRepository goalEtcRepository;
	private final UserRepository userRepository;

	@Transactional
	public GoalResponseDto createGoal(GoalCreateRequest requestDto, Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

		// Goal 저장
		Goal newGoal = requestDto.toEntity(user);
		Goal savedGoal = goalRepository.save(newGoal);

		GoalDetailRequest detail = requestDto.getDetail();
		saveGoalDetail(detail, savedGoal, user);

		return GoalResponseDto.from(savedGoal);
	}

	private void saveGoalDetail(GoalDetailRequest detail, Goal savedGoal, User user) {
		if (detail instanceof GoalRetirementRequest retirementDetail) {
			GoalRetirement goalRetirement = retirementDetail.toEntity(savedGoal, user.getBirthDate().getYear());
			goalRetirementRepository.save(goalRetirement);
		} else if (detail instanceof GoalHousingRequest housingDetail) {
			GoalHousing goalHousing = housingDetail.toEntity(savedGoal);
			goalHousingRepository.save(goalHousing);
		} else if (detail instanceof GoalEtcRequest etcDetail) {
			GoalEtc goalEtc = etcDetail.toEntity(savedGoal);
			goalEtcRepository.save(goalEtc);
		} else {
			throw new CustomException(ErrorCode.GOAL_INVALID_CATEGORY);
		}

	}
}
