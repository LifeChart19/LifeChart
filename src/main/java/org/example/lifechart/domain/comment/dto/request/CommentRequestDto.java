package org.example.lifechart.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentRequestDto {

	@Schema(description = "내용", example = "와 벌써 이만큼 달성하셨네요")
	@NotBlank(message = "내용은 필수 입력입니다.")
	private String contents;
}
