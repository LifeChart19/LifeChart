package org.example.lifechart.domain.goal.dto.request;

import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.enums.HousingType;
import org.example.lifechart.validation.annotation.ValidGoalPeriod;
import org.example.lifechart.validation.support.HaSGoalPeriod;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidGoalPeriod
public class GoalHousingCalculateRequest implements HaSGoalPeriod {

	@Schema(description = "시작일", example = "2025-06-30T00:00:00")
	@NotNull(message = "시작일은 필수 입력값입니다.")
	private LocalDateTime startAt;

	@Schema(description = "종료일", example = "2025-07-31T00:00:00")
	@NotNull(message = "종료일은 필수 입력값입니다.")
	private LocalDateTime endAt;

	@Schema(description = "지역", example = "서울시")
	@NotBlank(message = "지역은 필수 입력값입니다.")
	private String region;

	@Schema(description = "세부 지역", example = "서남권")
	@NotBlank(message = "세부 지역은 필수 입력값입니다.")
	private String subregion;

	@Schema(description = "주거 타입", example = "APARTMENT / VILLA / OFFICETEL")
	@JsonProperty("housingType")
	@NotNull(message = "주거 타입은 필수 입력값입니다.")
	private HousingType housingType;

	@Schema(description = "면적", example = "100")
	@Min(10) // 최소 10 m^2
	@Max(500) // 최대 500 m^2 약 160평. (넉넉하게 설정한 최고급 수준)
	@NotNull(message = "면적은 필수 입력값입니다.")
	private Long area;

	@Override
	public LocalDateTime getStartAt() {
		return startAt;
	}

	@Override
	public LocalDateTime getEndAt() {
		return endAt;
	}
}
