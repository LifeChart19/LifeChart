package org.example.lifechart.common.response;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReasonDto {

	private HttpStatus httpStatus;
	private final boolean isSuccess;
	private final String message;

}