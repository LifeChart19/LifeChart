package org.example.lifechart.domain.goal.service;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalDetailRequest;
import org.example.lifechart.domain.goal.dto.request.GoalEtcRequest;
import org.example.lifechart.domain.goal.dto.request.GoalHousingRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequest;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.fetcher.GoalDetailFetcherFactory;
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
public class GoalServiceImpl implements GoalService {

	private final GoalRepository goalRepository;
	private final GoalRetirementRepository goalRetirementRepository;
	private final GoalHousingRepository goalHousingRepository;
	private final GoalEtcRepository goalEtcRepository;
	private final UserRepository userRepository;
	private final GoalDetailFetcherFactory goalDetailFetcherFactory;

	@Transactional
	@Override
	public GoalResponse createGoal(GoalCreateRequest requestDto, Long userId) {
		User user = validUser(userId);

		// Goal 저장
		Goal newGoal = requestDto.toEntity(user);
		Goal savedGoal = goalRepository.save(newGoal);

		GoalDetailRequest detail = requestDto.getDetail();
		saveGoalDetail(detail, savedGoal, user);

		return GoalResponse.from(savedGoal);
	}

	@Override
	public GoalInfoResponse findGoal(Long goalId, Long userId) {
		Goal goal = validGoal(goalId, userId);
		GoalDetailInfoResponse goalDetail = goalDetailFetcherFactory.getDetail(goal);

		return GoalInfoResponse.from(goal, goalDetail);
	}

	@Transactional
	@Override
	public void deleteGoal(Long goalId, Long userId) {
		User user = validUser(userId);
		Goal goal = validGoal(goalId, user.getId());
		if (isDeleted(goal)) {
			throw new CustomException(ErrorCode.GOAL_ALREADY_DELETED);
		}
		goal.delete();
	}

	private User validUser(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
		return user;
	}

	private Goal validGoal(Long goalId, Long userId) {
		Goal goal = goalRepository.findByIdAndUserId(goalId, userId).
			orElseThrow(()-> new CustomException(ErrorCode.GOAL_NOT_FOUND));
		return goal;
	}

	private boolean isDeleted(Goal goal) {
		return (goal.getStatus() == Status.DELETED);
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
