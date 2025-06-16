package org.example.lifechart.domain.shareGoal.service;

import java.util.List;

import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;

public interface ShareGoalService {
	ShareGoalCursorResponseDto getShareGoals(Long authId, Long cursorId, int size, Category category, Share share);

	List<ShareGoalResponseDto> getShareGoalsToUser(Long authId, Long userId);
}

