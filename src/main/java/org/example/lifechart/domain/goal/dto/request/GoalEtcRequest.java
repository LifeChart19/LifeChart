package org.example.lifechart.domain.goal.dto.request;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalEtc;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
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
public class GoalEtcRequest implements GoalDetailRequest {

	@Schema(description = "테마", example = "자동차, 여행")
	@NotBlank(message = "테마는 필수 입력입니다.")
	private String theme;

	@Schema(description = "필요 금액", example = "100000000")
	@Max(1_000_000_000_000L) // 최대 1조
	@NotNull(message = "필요 금액은 필수 입력입니다.")
	private Long expectedPrice;

}
