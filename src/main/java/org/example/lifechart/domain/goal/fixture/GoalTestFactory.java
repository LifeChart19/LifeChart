package org.example.lifechart.domain.goal.fixture;

import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.user.entity.User;

public class GoalTestFactory {

	/**
	 * 모든 필드에 기본값이 들어간 Goal 인스턴스 반환
	 */
	public static Goal createTestGoal(User user) {
		return testBuilder(user, "테스트 목표").build();
	}

	/**
	 * Builder 반환으로 유연한 테스트 설정 가능
	 */
	public static Goal.GoalBuilder testBuilder(User user, String title) {
		return Goal.builder()
			.user(user)
			.title(title)
			.category(Category.RETIREMENT)
			.targetAmount(1_000_000L)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusMonths(6))
			.progressRate(0.0f)
			.status(Status.ACTIVE)
			.share(Share.PRIVATE);
	}
}
