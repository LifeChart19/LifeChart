package org.example.lifechart.common.enums;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode{

    // Auth
    SUCCESS_USER_LOGIN(HttpStatus.OK,"로그인을 성공하였습니다."),
    SUCCESS_USER_LOGOUT(HttpStatus.OK,"로그아웃 되었습니다."),


    // User
    CREATE_USER_SUCCESS(HttpStatus.CREATED,"유저를 생성했습니다."),
    UPDATE_USER_SUCCESS(HttpStatus.OK,"유저를 수정했습니다."),
    DELETE_USER_SUCCESS(HttpStatus.OK,"유저탈퇴가 완료되었습니다."),

    // Post
    CREATE_POST_SUCCESS(HttpStatus.CREATED, "게시글이 생성되었습니다."),
    GET_ALL_POSTS_SUCCESS(HttpStatus.OK, "게시글 목록을 조회합니다."),
    SEARCH_POST_SUCCESS(HttpStatus.FOUND,"게시글을 검색합니다."),
    UPDATE_POST_SUCCESS(HttpStatus.OK,"게시글을 수정했습니다."),
    DELETE_POST_SUCCESS(HttpStatus.OK, "게시글을 삭제했습니다."),

    // Keyword
    GET_POPULAR_KEYWORDS_SUCCESS(HttpStatus.OK,"인기 검색어를 조회합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(HttpStatus httpStatus, String message) {
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

}
