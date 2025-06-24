package org.example.lifechart.domain.goal.dto.request;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.enums.HousingType;

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
public class GoalHousingRequest implements GoalDetailRequest {

	@Schema(description = "지역", example = "서울특별시")
	@NotBlank(message = "지역은 필수 입력입니다.")
	private String region;

	@Schema(description = "세부 지역", example = "서남권")
	@NotBlank(message = "세부 지역은 필수 입력입니다.")
	private String subregion;

	@Schema(description = "면적", example = "84")
	@NotNull(message = "면적은 필수 입력입니다.")  // Long 타입이므로 NotNull 사용
	@Min(10) // 최소 10 m^2
	@Max(500) // 최대 500 m^2 약 160평. (넉넉하게 설정한 최고급 수준)
	private Long area;

	@Schema(description = "주거 타입", example = "APARTMENT")
	@NotNull(message = "주거 타입은 필수 입력입니다.")  // Enum 타입이므로 NotNull 사용
	@JsonProperty("housingType")
	private HousingType housingType;

}
