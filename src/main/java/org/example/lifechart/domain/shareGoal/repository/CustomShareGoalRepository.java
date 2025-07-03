package org.example.lifechart.domain.shareGoal.repository;

import java.time.Period;
import java.util.List;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.shareGoal.enums.Sort;

public interface CustomShareGoalRepository {
	List<Goal> findByAuthIdAndCursorAndFilters(Long authId, Goal cursorGoal, Long cursorId, int size, Category category,
		Share share, Sort sort, Period period);

	List<Goal> findByAuthIdAndUserId(Long authId, Long userId);

	List<Goal> findByAuthIdAndCursorAndTitleContaining(Long authId, Long cursorId, int size, String keyword);

}
