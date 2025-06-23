package org.example.lifechart.domain.shareGoal.dto.response;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

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
	private String remainingPeriod;
	private Float progressRate;
	private Share share;
	private List<String> tags;
	private LocalDateTime createdAt;

	public static ShareGoalResponseDto from(Goal goal) {
		Period period = Period.between(goal.getStartAt().toLocalDate(), goal.getEndAt().toLocalDate());
		int years = period.getYears();
		int months = period.getMonths();
		int days = period.getDays();
		String periodFormat = String.format("%d년 %d개월 %d일", years, months, days);

		return ShareGoalResponseDto.builder()
			.goalId(goal.getId())
			.nickname(goal.getUser().getNickname())
			.title(goal.getTitle())
			.category(goal.getCategory())
			.targetAmount(goal.getTargetAmount())
			.remainingPeriod(periodFormat)
			.progressRate(null)
			.share(goal.getShare())
			.tags(goal.getTags())
			.createdAt(goal.getCreatedAt())
			.build();
	}
}
