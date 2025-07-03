package org.example.lifechart.domain.shareGoal.repository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import org.example.lifechart.domain.follow.entity.QFollow;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.QGoal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.shareGoal.enums.Sort;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomShareGoalRepositoryImpl implements CustomShareGoalRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Goal> findByAuthIdAndCursorAndFilters(
		Long authId, Goal cursorGoal, Long cursorId, int size, Category category, Share share, Sort sort, Period period
	) {
		QGoal goal = QGoal.goal;
		QFollow follow = QFollow.follow;
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (cursorId != null && sort == Sort.RECENT) {
			booleanBuilder.and(goal.id.lt(cursorId));
		}

		// POPULAR 일땐 Id값이 뒤죽박죽이기 때문에 cursorId보다 점수가 낮은 것을 보여주거나,
		// 점수가 같다면 goalId가 cursorId보다 미만인 경우를 보여주도록 함
		if (cursorId != null && sort == Sort.POPULAR) {
			long cursorScore = cursorGoal.getCommentCount() + cursorGoal.getLikeCount();
			Long cursorGoalId = cursorGoal.getId();

			NumberTemplate<Long> score = Expressions.numberTemplate(Long.class, "({0} + {1})",
				goal.commentCount, goal.likeCount);

			booleanBuilder.and(
				score.lt(cursorScore)
					.or(score.eq(cursorScore).and(goal.id.lt(cursorGoalId)))
			);

		}
		if (category != null) {
			booleanBuilder.and(goal.category.eq(category));
		}
		BooleanExpression sharedCondition = shareCondition(authId, share, goal, follow);
		if (sharedCondition != null) {
			booleanBuilder.and(sharedCondition);
		}
		if (period != null) {
			LocalDateTime term = LocalDateTime.now().minus(period);
			booleanBuilder.and(goal.createdAt.goe(term));
		}
		booleanBuilder.and(goal.status.eq(Status.ACTIVE));
		return jpaQueryFactory
			.selectFrom(goal)
			.leftJoin(follow).on(follow.receiver.id.eq(goal.user.id))
			.where(booleanBuilder)
			.orderBy(getOrderSpecifier(sort))
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

	// sort에 따라 다르게 정렬
	private OrderSpecifier<?>[] getOrderSpecifier(Sort sort) {
		QGoal goal = QGoal.goal;

		switch (sort) {
			case POPULAR:
				return new OrderSpecifier[] {
					Expressions.numberTemplate(Long.class, "({0} + {1})",
						goal.likeCount, goal.commentCount).desc(), goal.id.desc()
				};
			case RECENT:
			default:
				return new OrderSpecifier[] {goal.id.desc()};
		}
	}
}
