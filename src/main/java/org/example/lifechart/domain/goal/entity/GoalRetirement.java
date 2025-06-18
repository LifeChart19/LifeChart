package org.example.lifechart.domain.goal.entity;

import java.time.LocalDate;

import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.goal.dto.request.GoalRetirementRequest;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.goal.helper.GoalDateHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class GoalRetirement extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "goal_id", nullable = false)
	private Goal goal;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RetirementType retirementType;

	@Column(nullable = false)
	private LocalDate expectedDeathDate;

	@Column(nullable = false)
	private Long monthlyExpense;

	public void update(GoalRetirementRequest request, int birthYear) {
		this.retirementType = request.getRetirementType();
		this.expectedDeathDate = GoalDateHelper.toExpectedDeathDate(request.getExpectedLifespan(), birthYear);
		this.monthlyExpense = request.getMonthlyExpense();
	}
}
