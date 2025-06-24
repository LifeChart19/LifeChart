package org.example.lifechart.domain.shareGoal.repository;

import java.util.List;

import org.example.lifechart.domain.follow.entity.QFollow;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.QGoal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomShareGoalRepositoryImpl implements CustomShareGoalRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Goal> findByAuthIdAndCursorAndFilters(
		Long authId, Long cursorId, int size, Category category, Share share
	) {
		QGoal goal = QGoal.goal;
		QFollow follow = QFollow.follow;
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		if (cursorId != null) {
			booleanBuilder.and(goal.id.lt(cursorId));
		}
		if (category != null) {
			booleanBuilder.and(goal.category.eq(category));
		}
		BooleanExpression sharedCondition = shareCondition(authId, share, goal, follow);
		if (sharedCondition != null) {
			booleanBuilder.and(sharedCondition);
		}
		booleanBuilder.and(goal.status.eq(Status.ACTIVE));
		return jpaQueryFactory
			.selectFrom(goal)
			.leftJoin(follow).on(follow.receiver.id.eq(goal.user.id))
			.where(booleanBuilder)
			.orderBy(goal.id.desc())
			.limit(size)
			.fetch();
	}

	@Override
	public List<Goal> findByAuthIdAndUserId(Long authId, Long userId) {
		QGoal goal = QGoal.goal;
		QFollow follow = QFollow.follow;
		return jpaQueryFactory
			.selectFrom(goal)
			.leftJoin(follow).on(
				follow.receiver.id.eq(goal.user.id).and(follow.requester.id.eq(authId))
			)
			.where(
				goal.user.id.eq(userId),
				goal.share.eq(Share.ALL)
					.or(follow.id.isNotNull().and(goal.share.eq(Share.FOLLOWER))),
				goal.status.eq(Status.ACTIVE)
			)
			.orderBy(goal.id.desc())
			.fetch();
	}

	@Override
	public List<Goal> findByAuthIdAndCursorAndTitleContaining(Long authId, Long cursorId, int size, String keyword) {
		QGoal goal = QGoal.goal;
		QFollow follow = QFollow.follow;
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		if (cursorId != null) {
			booleanBuilder.and(goal.id.lt(cursorId));
		}
		booleanBuilder.and(
			(follow.id.isNotNull().and(goal.share.eq(Share.FOLLOWER)))
				.or(goal.share.eq(Share.ALL))
		);
		booleanBuilder.and(goal.title.contains(keyword));
		booleanBuilder.and(goal.status.eq(Status.ACTIVE));
		return jpaQueryFactory
			.selectFrom(goal)
			.leftJoin(follow).on(
				follow.receiver.id.eq(goal.user.id).and(follow.requester.id.eq(authId))
			)
			.where(booleanBuilder)
			.orderBy(goal.id.desc())
			.limit(size)
			.fetch();
	}

	private BooleanExpression shareCondition(Long authId, Share share, QGoal goal, QFollow follow) {
		if (share == null) {
			// default: 공유 상태가 ALL인 것과 로그인한 유저가 팔로우한 사람의 목표(공유 상태가 FOLLOWER)들이 나오는 상태
			return (follow.requester.id.eq(authId).and(goal.share.eq(Share.FOLLOWER)))
				.or(goal.share.eq(Share.ALL));
		}
		if (share == Share.ALL) {
			return goal.share.eq(Share.ALL);
		}
		if (share == Share.FOLLOWER) {
			return follow.requester.id.eq(authId).and(goal.share.eq(Share.FOLLOWER));
		}
		return null;
	}
}
