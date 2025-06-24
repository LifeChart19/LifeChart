package org.example.lifechart.domain.goal.repository;

import java.util.List;

import org.example.lifechart.domain.goal.dto.request.GoalSearchCondition;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.QGoal;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class CustomGoalRepositoryImpl implements CustomGoalRepository{

	private final JPAQueryFactory queryFactory;

	public CustomGoalRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<Goal> searchGoalsWithCursor(Long userId, GoalSearchCondition condition) {
		QGoal goal = QGoal.goal;

		return queryFactory
			.selectFrom(goal)
			.where(
				goal.user.id.eq(userId),
				condition.cursorId() != null ? goal.id.lt(condition.cursorId()) : null,
				condition.status() != null ? goal.status.eq(condition.status()) : null,
				condition.category() != null ? goal.category.eq(condition.category()) : null,
				condition.share() != null ? goal.share.eq(condition.share()) : null
			)
			.orderBy(goal.id.desc())
			.limit(condition.size() +1)
			.fetch();
	}
}
