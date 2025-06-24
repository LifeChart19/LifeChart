package org.example.lifechart.domain.goal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalDetailRequest;
import org.example.lifechart.domain.goal.dto.request.GoalEtcRequest;
import org.example.lifechart.domain.goal.dto.request.GoalHousingRequest;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequest;
import org.example.lifechart.domain.goal.dto.request.GoalUpdateRequest;
import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalResponse;
import org.example.lifechart.domain.goal.dto.response.GoalSummaryResponse;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalEtc;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.fetcher.GoalDetailFetcherFactory;
import org.example.lifechart.domain.goal.repository.GoalEtcRepository;
import org.example.lifechart.domain.goal.repository.GoalHousingRepository;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.goal.repository.GoalRetirementRepository;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
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
		GoalDetailRequest detail = requestDto.getDetail();

		validateCategoryAndDetail(requestDto.getCategory(), detail);

		// Goal Entity 반환
		Goal newGoal = Goal.from(requestDto, user);
		Goal savedGoal = goalRepository.save(newGoal);
		saveGoalDetail(detail, savedGoal, user);

		return GoalResponse.from(savedGoal);
	}

	@Override
	@Transactional(readOnly = true)
	public GoalInfoResponse findGoal(Long goalId, Long userId) {
		Goal goal = validGoal(goalId, userId);
		GoalDetailInfoResponse goalDetail = goalDetailFetcherFactory.getDetail(goal);

		return GoalInfoResponse.from(goal, goalDetail);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GoalSummaryResponse> findMyGoals(Long userId) {
		User user = validUser(userId);
		return goalRepository.findAllByUserId(user.getId())
			.stream()
			.map(GoalSummaryResponse::from)
			.collect(Collectors.toList());
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

	@Transactional
	@Override
	public GoalResponse updateGoal(GoalUpdateRequest request, Long goalId, Long userId) {
		User user = validUser(userId);
		Goal savedGoal = validGoal(goalId, userId);
		if (isDeleted(savedGoal)) {
			throw new CustomException(ErrorCode.GOAL_ALREADY_DELETED);
		}

		GoalDetailRequest detail = request.getDetail();
		validateCategoryAndDetail(savedGoal.getCategory(), detail);

		savedGoal.update(request); // 목표 수정 Entity 반영 > DB 갱신
		updateGoalDetail(detail, savedGoal.getId(), user); // 목표 상세 수정 Entity > DB 갱신

		return GoalResponse.from(savedGoal);
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

	private void validateCategoryAndDetail(Category category, GoalDetailRequest detail) {
		boolean isValid = switch (category) {
			case HOUSING -> detail instanceof GoalHousingRequest;
			case RETIREMENT -> detail instanceof GoalRetirementRequest;
			case ETC -> detail instanceof GoalEtcRequest;
		};

		if (!isValid) {
			throw new CustomException(ErrorCode.GOAL_CATEGORY_DETAIL_MISMATCH);
		}
	}

	private boolean isDeleted(Goal goal) {
		return (goal.getStatus() == Status.DELETED);
	}

	private void saveGoalDetail(GoalDetailRequest detail, Goal savedGoal, User user) {
		if (detail instanceof GoalRetirementRequest retirementDetail) {
			GoalRetirement goalRetirement = GoalRetirement.from(savedGoal, retirementDetail, user.getBirthDate().getYear());
			goalRetirementRepository.save(goalRetirement);
		} else if (detail instanceof GoalHousingRequest housingDetail) {
			GoalHousing goalHousing = GoalHousing.from(savedGoal, housingDetail);
			goalHousingRepository.save(goalHousing);
		} else if (detail instanceof GoalEtcRequest etcDetail) {
			GoalEtc goalEtc = GoalEtc.from(savedGoal, etcDetail);
			goalEtcRepository.save(goalEtc);
		} else {
			throw new CustomException(ErrorCode.GOAL_INVALID_CATEGORY);
		}
	}

	private void updateGoalDetail(GoalDetailRequest detail, Long goalId, User user) {
		if (detail instanceof GoalRetirementRequest retirementDetail) {
			GoalRetirement goalRetirement = goalRetirementRepository.findByGoalId(goalId)
				.orElseThrow(() -> new CustomException(ErrorCode.GOAL_RETIREMENT_NOT_FOUND));
			goalRetirement.update(retirementDetail, user.getBirthDate().getYear());
		} else if (detail instanceof GoalHousingRequest housingDetail) {
			GoalHousing goalHousing = goalHousingRepository.findByGoalId(goalId)
				.orElseThrow(() -> new CustomException(ErrorCode.GOAL_HOUSING_NOT_FOUND));
			goalHousing.update(housingDetail);
		} else if (detail instanceof GoalEtcRequest etcDetail) {
			GoalEtc goalEtc = goalEtcRepository.findByGoalId(goalId)
				.orElseThrow(() -> new CustomException(ErrorCode.GOAL_ETC_NOT_FOUND));
			goalEtc.update(etcDetail);
		}
	}
}
