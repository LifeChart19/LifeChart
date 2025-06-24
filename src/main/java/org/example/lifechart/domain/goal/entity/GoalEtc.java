package org.example.lifechart.domain.goal.entity;

import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.goal.dto.request.GoalCreateRequest;
import org.example.lifechart.domain.goal.dto.request.GoalEtcRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class GoalEtc extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "goal_id", nullable = false)
	private Goal goal;

	@Column(nullable = false)
	private String theme;

	@Column(nullable = false)
	private Long expectedPrice;

	public static GoalEtc from(Goal goal, GoalEtcRequest request) {
		return GoalEtc.builder()
			.goal(goal)
			.theme(request.getTheme())
			.expectedPrice(request.getExpectedPrice())
			.build();
	}

	public void update(GoalEtcRequest request) {
		this.theme = request.getTheme();
		this.expectedPrice = request.getExpectedPrice();;
	}
}