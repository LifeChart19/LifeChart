package org.example.lifechart.domain.goal.dto.request;

import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.validation.annotation.ValidGoalPeriod;
import org.example.lifechart.validation.support.HaSGoalPeriod;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class GoalRetirementCalculateRequest implements HaSGoalPeriod {

	@Schema(description = "목표 시작일", example = "2025-07-01T00:00:00") // 클라이언트가 2025-07-01 이렇게 입력해도 뒤에 T00:00:00 자동 반환
	@NotNull(message = "시작일은 필수 입력입니다.")
	private LocalDateTime startAt;

	@Schema(description = "목표 종료일", example = "2030-06-30T00:00:00") // 클라이언트가 2025-07-01 이렇게 입력해도 뒤에 T00:00:00 자동 반환
	@NotNull(message = "종료일은 필수 입력입니다.")
	private LocalDateTime endAt;

	@Schema(description = "기대 수명", example = "90")
	@Max(150L) // 최대 150세까지 입력 가능
	@NotNull(message = "기대 수명은 필수 입력입니다.")
	private Long expectedLifespan;

	@Schema(description = "월 지출", example = "3000000")
	@Min(1L) // 최소 1원
	@Max(30_000_000L) // 최대 3,000만원
	@NotNull(message = "월 지출은 필수 입력입니다.")
	private Long monthlyExpense;

	@Schema(description = "은퇴 타입", example = "COUPLE / SOLO")
	@JsonProperty("retirementType")
	@NotNull(message = "은퇴 타입은 필수 입력입니다.")
	private RetirementType retirementType;

	@Override
	public LocalDateTime getStartAt() {
		return startAt;
	}

	@Override
	public LocalDateTime getEndAt() {
		return endAt;
	}
}