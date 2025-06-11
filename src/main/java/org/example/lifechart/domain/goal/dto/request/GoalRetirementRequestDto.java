package org.example.lifechart.domain.goal.dto.request;

import java.time.LocalDate;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.goal.helper.GoalDateHelper;
import org.example.lifechart.domain.goal.service.StandardValueService;
import org.example.lifechart.domain.user.entity.User;

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
public class GoalRetirementRequestDto implements GoalDetailRequestDto{

	@Schema(description = "월 지출", example = "3000000")
	@Min(1L) // 최소 1원
	@Max(30_000_000L) // 최대 3,000만원
	@NotNull(message = "월 지출은 필수 입력입니다.")
	private Long monthlyExpense;

	@Schema(description = "은퇴 타입", example = "COUPLE / SOLO")
	@JsonProperty("retirementType")
	@NotNull(message = "은퇴 타입은 필수 입력입니다.")
	private RetirementType retirementType;

	@Schema(description = "기대 수명", example = "90")
	@Max(150L) // 최대 150세까지 입력 가능
	@NotNull(message = "기대 수명은 필수 입력입니다.")
	private Long expectedLifespan; // endAt보다 크거나 같음을 검증하는 로직 필요. 어디에서 할지는 고민 필요

	public GoalRetirementRequestDto withFallbacks(User user, StandardValueService standardValueService) {
		return GoalRetirementRequestDto.builder()
			.retirementType(this.retirementType != null ? this.retirementType : RetirementType.COUPLE) // 은퇴 타입 기본 설정: 부부
			.monthlyExpense(this.monthlyExpense != null ? this.monthlyExpense : standardValueService.getAverageMonthlyExpense(retirementType)) // 은퇴 타입에 따라 월 평균 지출 기본 설정
			.expectedLifespan(this.expectedLifespan != null ? this.expectedLifespan : standardValueService.getExpectedLifespan(user.getGender(), LocalDate.now().getYear())) // 성별에 따른 평균 수명 기본 설정
			.build();
	}

	public GoalRetirement toEntity(Goal goal, int birthYear) {
		LocalDate expectedDeathDate = GoalDateHelper.toExpectedDeathDate(expectedLifespan, birthYear);

		return GoalRetirement.builder()
			.goal(goal)
			.monthlyExpense(monthlyExpense)
			.retirementType(retirementType)
			.expectedDeathDate(expectedDeathDate)
			.build();
	}
}
