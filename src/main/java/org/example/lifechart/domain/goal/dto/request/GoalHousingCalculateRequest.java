package org.example.lifechart.domain.goal.dto.request;

import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.enums.HousingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalHousingCalculateRequest {
	private LocalDateTime endAt;
	private String region;
	private String subregion;
	private HousingType housingType;
	private Long area;
}
