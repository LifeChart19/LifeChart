package org.example.lifechart.domain.shareGoal.service;

import java.util.List;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShareGoalServiceImpl implements ShareGoalService {
	private final GoalRepository goalRepository;
	private final UserRepository userRepository;

	@Transactional
	@Override
	public ShareGoalCursorResponseDto getShareGoals(
		Long authId, Long cursorId, int size, Category category, Share share
	) {

		User foundUser = userRepository.findByIdAndDeletedAtIsNull(authId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<ShareGoalResponseDto> shareGoalList = goalRepository.findByAuthIdAndCursorAndFilters(
				foundUser.getId(), cursorId, size, category, share
			)
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();

		return ShareGoalCursorResponseDto.from(shareGoalList);
	}

	@Transactional
	@Override
	public List<ShareGoalResponseDto> getShareGoalsToUser(Long authId, Long userId) {

		User foundAuthUser = userRepository.findByIdAndDeletedAtIsNull(authId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		User foundUser = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		return goalRepository.findByAuthIdAndUserId(foundAuthUser.getId(), foundUser.getId())
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
	}
}
