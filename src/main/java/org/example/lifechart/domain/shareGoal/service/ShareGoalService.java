package org.example.lifechart.domain.shareGoal.service;

import java.util.List;

import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.shareGoal.dto.reqeust.ShareGoalSearchRequestDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalSearchResponseDto;

public interface ShareGoalService {
	ShareGoalCursorResponseDto getShareGoals(Long authId, Long cursorId, int size, Category category, Share share);

	List<ShareGoalResponseDto> getShareGoalsToUser(Long authId, Long userId);

	ShareGoalCursorResponseDto searchShareGoals(Long authId, Long cursorId, int size, String keyword);

	void plusSearchKeyword(Long authId, ShareGoalSearchRequestDto shareGoalSearchRequestDto);

	List<ShareGoalSearchResponseDto> searchTop10Keyword(Long authId);

}

