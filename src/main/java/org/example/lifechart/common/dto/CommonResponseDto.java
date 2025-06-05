package org.example.lifechart.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.example.lifechart.common.enums.BaseCode;
import org.springframework.http.HttpStatus;

@Getter
public class CommonResponseDto<T> {
    private final HttpStatus httpStatus;
    private final String message;
    // null 인 경우 응답에서 제외할 수 있다 (주석처리하면 null 값 포함해서 응답 반환)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private CommonResponseDto(HttpStatus httpStatus, String message, T data) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponseDto<T> of(BaseCode code, T data) {
        return new CommonResponseDto<>(code.getStatus(), code.getMessage(), data);
    }

    public static <T> CommonResponseDto<T> of(BaseCode code) {
        return of(code, null);
    }

}
