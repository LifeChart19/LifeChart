package org.example.lifechart.domain.goal.entity;

import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.goal.dto.request.GoalHousingRequest;
import org.example.lifechart.domain.goal.enums.HousingType;

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
public class GoalHousing extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "goal_id", nullable = false)
	private Goal goal;

	@Column(nullable = false)
	private String region;

	@Column(nullable = false)
	private String subregion;

	@Column(nullable = false)
	private Long area;

	@Enumerated(EnumType.STRING)
	private HousingType housingType;

	public static GoalHousing from(Goal goal, GoalHousingRequest request) {
		return GoalHousing.builder()
			.goal(goal)
			.region(request.getRegion())
			.subregion(request.getSubregion())
			.area(request.getArea())
			.housingType(request.getHousingType())
			.build();
	}

	public void update(GoalHousingRequest request) {
		this.region = request.getRegion();
		this.subregion = request.getSubregion();
		this.area = request.getArea();
		this.housingType = request.getHousingType();
	}
}
