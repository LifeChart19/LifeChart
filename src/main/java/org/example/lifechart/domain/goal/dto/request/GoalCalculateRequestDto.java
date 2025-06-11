package org.example.lifechart.domain.goal.dto.request;

import java.time.LocalDateTime;

import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.validation.annotation.ValidGoalPeriod;
import org.example.lifechart.validation.support.HaSGoalPeriod;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class GoalCalculateRequestDto implements HaSGoalPeriod {

	@Schema(description = "목표 카테고리", example = "RETIREMENT / HOUSING / ETC")
	@NotNull(message = "카테고리 정보는 필수 입력입니다.")
	private Category category;

	@Schema(description = "목표 시작일", example = "2025-06-03T00:00:00") // 클라이언트가 2025-06-03 이렇게 입력해도 뒤에 T00:00:00 자동 반환)
	private LocalDateTime startAt;

	@Schema(description = "목표 종료일", example = "2025-07-01T00:00:00") // 클라이언트가 2025-07-01 이렇게 입력해도 뒤에 T00:00:00 자동 반환)
	@NotNull(message = "목표 종료일은 필수 입력입니다.")
	private LocalDateTime endAt;

	@Schema(description = "목표 상세", example = "housing: {} / retirement: {} / etc : {}")
	@NotNull(message = "목표 상세 정보는 필수 입력입니다.")
	@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
	@JsonSubTypes({
		@JsonSubTypes.Type(value = GoalHousingDetailRequestDto.class, name = "housing"),
		@JsonSubTypes.Type(value = GoalRetirementDetailRequestDto.class, name = "retirement"),
		@JsonSubTypes.Type(value = GoalEtcDetailRequestDto.class, name = "etc")
	})
	private GoalDetailRequestDto detail;

	@Override
	public LocalDateTime getStartAt() {
		return startAt;
	}

	@Override
	public LocalDateTime getEndAt() {
		return endAt;
	}
}
