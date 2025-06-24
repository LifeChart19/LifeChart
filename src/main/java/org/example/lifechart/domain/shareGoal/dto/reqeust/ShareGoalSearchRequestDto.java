package org.example.lifechart.domain.shareGoal.dto.reqeust;

import java.util.List;

import org.example.lifechart.domain.goal.enums.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ShareGoalSearchRequestDto {
	@NotBlank(message = "검색어는 필수 입력입니다.")
	private String keyword;
	@NotEmpty(message = "태그는 필수 입력입니다.")
	private List<@NotBlank String> tags;
	@NotBlank(message = "카테고리는 필수 입력입니다.")
	private Category category;
}
