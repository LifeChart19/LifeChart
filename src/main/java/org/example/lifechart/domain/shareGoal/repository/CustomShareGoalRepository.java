package org.example.lifechart.domain.shareGoal.repository;

import java.util.List;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;

public interface CustomShareGoalRepository {
	List<Goal> findByAuthIdAndCursorAndFilters(Long authId, Long cursorId, int size, Category category, Share share);

	List<Goal> findByAuthIdAndUserId(Long authid, Long userId);

}
