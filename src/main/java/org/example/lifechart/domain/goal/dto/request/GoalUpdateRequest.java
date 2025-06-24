package org.example.lifechart.domain.goal.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import org.example.lifechart.domain.goal.enums.Share;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalUpdateRequest {

	@Schema(description = "목표명", example = "강남 집사기")
	@NotBlank(message = "목표명은 필수 입력입니다.")
	private String title;

	@Schema(description = "목표 시작일", example = "2025-07-01T00:00:00") // 클라이언트가 2025-07-01 이렇게 입력해도 뒤에 T00:00:00 자동 반환
	private LocalDateTime startAt; // nullable.

	@Schema(description = "목표 종료일", example = "2030-06-30T00:00:00") // 클라이언트가 2025-07-01 이렇게 입력해도 뒤에 T00:00:00 자동 반환
	@NotNull(message = "종료일은 필수 입력입니다.")
	private LocalDateTime endAt;

	@Schema(description = "생성할 목표와 연동할 시뮬레이션 ID 목록(선택 사항)", example = "[11, 19, 21]")
	private List<Long> simulationIds;

	@Schema(description = "카테고리별 상세 정보", example = "housing: {} / retirement : {} / etc: {}")
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
	@JsonSubTypes({
		@JsonSubTypes.Type(value = GoalHousingRequest.class, name = "housing"),
		@JsonSubTypes.Type(value = GoalRetirementRequest.class, name = "retirement"),
		@JsonSubTypes.Type(value = GoalEtcRequest.class, name = "etc")
	})
	@NotNull(message = "카테고리 상세 정보는 필수 입력입니다.")
	private GoalDetailRequest detail;

	@Schema(description = "목표 금액", example = "850000000") // 계산 후 반환해주며, 유저가 수정 가능함
	@NotNull(message = "목표 금액은 필수 입력입니다.")
	private Long targetAmount;

	@Schema(description = "공유 설정", example = "ALL / FOLLOWER / PRIVATE")
	@JsonProperty("share")
	@NotNull(message = "공유 설정은 필수 입력입니다.")
	private Share share; // nullable

	@Schema(description = "태그", example = "[주거, 강남]")
	@NotEmpty(message = "태그는 필수 입력입니다.")
	private List<String> tags;
}
