package org.example.lifechart.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.common.dto.CommonResponseDto;
import org.example.lifechart.common.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(CommonResponseDto.of(e.getErrorCode()));
    }

    // @Valid 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(CommonResponseDto.of(ErrorCode.INVALID_INPUT_VALUE,
                        e.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto<Object>> handle(Exception e){
        log.error("error",e.getMessage());
        return ResponseEntity
                .status(500)
                .body(null);
    }

}
