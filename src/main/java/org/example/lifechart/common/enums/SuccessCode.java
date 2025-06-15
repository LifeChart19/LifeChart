package org.example.lifechart.common.enums;

import org.example.lifechart.common.response.ReasonDto;
import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode {

	GET_POPULAR_KEYWORDS_SUCCESS(HttpStatus.OK, "인기 검색어를 조회합니다."),

	// User (Line#: 20~49)
	CREATE_USER_SUCCESS(HttpStatus.CREATED, "유저를 생성했습니다."),
	UPDATE_USER_SUCCESS(HttpStatus.OK, "유저를 수정했습니다."),
	DELETE_USER_SUCCESS(HttpStatus.OK, "유저탈퇴가 완료되었습니다."),
	GET_USER_INFO_SUCCESS(HttpStatus.OK, "내 정보를 조회했습니다."),
	GET_USER_PROFILE_SUCCESS(HttpStatus.OK, "유저 프로필을 조회했습니다."),

	// Auth (Line#: 50~79)
	SUCCESS_USER_LOGIN(HttpStatus.OK, "로그인을 성공하였습니다."),
	SUCCESS_USER_LOGOUT(HttpStatus.OK, "로그아웃 되었습니다."),
	REFRESH_TOKEN_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공했습니다."),

	// Goal (Line#: 80~109)
	GOAL_CREATE_SUCCESS(HttpStatus.CREATED, "목표가 생성되었습니다."),
	GOAL_CALCULATE_SUCCESS(HttpStatus.OK, "목표 금액을 계산했습니다."),
	GOAL_RETIREMENT_ESTIMATE_SUCCESS(HttpStatus.OK, "은퇴 목표 기본값 반환에 성공했습니다."),
	GOAL_GET_INFO_SUCCESS(HttpStatus.OK, "개별 목표 조회에 성공했습니다."),

	// Simulation (Line#: 110~139)

	// Follow (Line#: 140~169)
	CREATE_FOLLOW_SUCCESS(HttpStatus.CREATED, "팔로우를 생성했습니다."),
	GET_ALL_FOLLOWERS_SUCCESS(HttpStatus.OK, "대상의 팔로워 목록을 조회합니다."),
	GET_ALL_FOLLOWING_SUCCESS(HttpStatus.OK, "대상의 팔로잉 목록을 조회합니다."),
	GET_FOLLOW_SUCCESS(HttpStatus.OK, "팔로우를 조회합니다."),
	DELETE_FOLLOW_SUCCESS(HttpStatus.NO_CONTENT, "팔로우를 취소합니다."),

	// Comment (Line#: 170~199)
	CREATE_COMMENT_SUCCESS(HttpStatus.CREATED, "댓글을 생성했습니다."),
	GET_ALL_COMMENT_SUCCESS(HttpStatus.OK, "댓글 목록을 조회합니다."),
	UPDATE_COMMENT_SUCCESS(HttpStatus.OK, "댓글을 수정합니다."),
	GET_COMMENT_SUCCESS(HttpStatus.OK, "댓글을 조회합니다."),
	DELETE_COMMENT_SUCCESS(HttpStatus.NO_CONTENT, "댓글을 삭제합니다."),

	// Like (Line#: 200~229)
	CREATE_LIKE_SUCCESS(HttpStatus.CREATED, "좋아요를 눌렀습니다."),
	GET_ALL_LIKE_SUCCESS(HttpStatus.OK, "좋아요 목록을 조회합니다."),
	GET_LIKE_SUCCESS(HttpStatus.OK, "좋아요를 조회합니다."),
	DELETE_LIKE_SUCCESS(HttpStatus.NO_CONTENT, "좋아요을 취소합니다."),

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
