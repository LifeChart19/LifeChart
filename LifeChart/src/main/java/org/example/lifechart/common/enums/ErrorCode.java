package org.example.lifechart.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseCode {

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"유저를 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일을 찾을 수 없습니다."),
    NOT_MATCH_USER(HttpStatus.UNAUTHORIZED, "유저가 일치하지 않습니다."),
    EXIST_SAME_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    EXIST_SAME_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST,"입력한 값의 형식이 잘못되었습니다."),

    // Auth
    NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    NOT_EXIST_COOKIE(HttpStatus.NOT_FOUND, "쿠키가 존재하지 않습니다."),
    SC_BAD_REQUEST(HttpStatus.BAD_REQUEST, "JWT 토큰이 없거나 일치하지 않습니다."),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }

}
