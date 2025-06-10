package org.example.lifechart.common.enums;

import org.example.lifechart.common.response.ReasonDto;
import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode{





    GET_POPULAR_KEYWORDS_SUCCESS(HttpStatus.OK,"인기 검색어를 조회합니다."),







    // User (Line#: 20~49)
    CREATE_USER_SUCCESS(HttpStatus.CREATED, "유저를 생성했습니다."),
    UPDATE_USER_SUCCESS(HttpStatus.OK,"유저를 수정했습니다."),
    DELETE_USER_SUCCESS(HttpStatus.OK,"유저탈퇴가 완료되었습니다."),
    GET_USER_INFO_SUCCESS(HttpStatus.OK, "내 정보를 조회했습니다."),
    GET_USER_PROFILE_SUCCESS(HttpStatus.OK, "유저 프로필을 조회했습니다."),

























    // Auth (Line#: 50~79)
    SUCCESS_USER_LOGIN(HttpStatus.OK,"로그인을 성공하였습니다."),
    SUCCESS_USER_LOGOUT(HttpStatus.OK,"로그아웃 되었습니다."),



























    // Goal (Line#: 80~109)
    CREATE_GOAL_SUCCESS(HttpStatus.CREATED, "목표가 생성되었습니다."),
    GET_ALL_POSTS_SUCCESS(HttpStatus.OK, "게시글 목록을 조회합니다."),
    SEARCH_POST_SUCCESS(HttpStatus.FOUND,"게시글을 검색합니다."),
    UPDATE_POST_SUCCESS(HttpStatus.OK,"게시글을 수정했습니다."),
    DELETE_POST_SUCCESS(HttpStatus.OK, "게시글을 삭제했습니다."),
























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

    SuccessCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.cachedReasonDto = ReasonDto.builder()
            .isSuccess(true)
            .httpStatus(httpStatus)
            .message(message)
            .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return cachedReasonDto;
    }

}
