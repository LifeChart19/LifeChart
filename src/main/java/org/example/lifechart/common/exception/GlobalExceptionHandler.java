package org.example.lifechart.common.exception;

import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.common.response.ReasonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<ReasonDto>> handleApiException(CustomException e) {
        return ApiResponse.onFailure(e.getErrorCode());
    }

}
