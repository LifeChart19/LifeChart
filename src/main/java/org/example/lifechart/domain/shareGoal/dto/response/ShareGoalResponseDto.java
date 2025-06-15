package org.example.lifechart.domain.shareGoal.dto.response;

import java.time.LocalDateTime;
import java.time.Period;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShareGoalResponseDto {
	private Long goalId;
	private String nickname;
	private String title;
	private Category category;
	private Long targetAmount;
	private Period remainingPeriod;
	private Float progressRate;
	private Share share;
	private LocalDateTime createdAt;

	public static ShareGoalResponseDto from(Goal goal) {
		Period period = Period.between(goal.getStartAt().toLocalDate(), goal.getEndAt().toLocalDate());

		return ShareGoalResponseDto.builder()
			.goalId(goal.getId())
			.nickname(goal.getUser().getNickname())
			.title(goal.getTitle())
			.category(goal.getCategory())
			.targetAmount(goal.getTargetAmount())
			.remainingPeriod(period)
			.progressRate(goal.getProgressRate())
			.share(goal.getShare())
			.createdAt(goal.getCreatedAt())
			.build();
	}
}
