package org.example.lifechart.common.enums;

import org.example.lifechart.common.response.ReasonDto;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    CUSTOM_ERROR_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "Custom Error"),






    // User (Line#: 20~49)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일을 찾을 수 없습니다."),
    NOT_MATCH_USER(HttpStatus.UNAUTHORIZED, "유저가 일치하지 않습니다."),
    EXIST_SAME_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    EXIST_SAME_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST,"입력한 값의 형식이 잘못되었습니다."),























    // Auth (Line#: 50~79)
    NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    NOT_EXIST_COOKIE(HttpStatus.NOT_FOUND, "쿠키가 존재하지 않습니다."),
    SC_BAD_REQUEST(HttpStatus.BAD_REQUEST, "JWT 토큰이 없거나 일치하지 않습니다."),

    // jwt
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT 서명이 유효하지 않습니다."),
    MALFORMED_JWT(HttpStatus.UNAUTHORIZED, "JWT 형식이 올바르지 않습니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다."),
    EMPTY_JWT(HttpStatus.UNAUTHORIZED, "JWT 토큰이 비어 있습니다."),

























    // Goal (Line#: 80~109)





























    // Simulation (Line#: 110~139)





























    // Follow (Line#: 140~169)





























    // Comment (Line#: 170~199)





























    // Like (Line#: 200~229)





























    // Notification (Line#: 230~259)





























    ;

    // 본 코드
    private final HttpStatus httpStatus;
    private final String message;
    private final ReasonDto cachedReasonDto;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.cachedReasonDto = ReasonDto.builder()
            .isSuccess(false)
            .httpStatus(httpStatus)
            .message(message)
            .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return cachedReasonDto;
    }
}
