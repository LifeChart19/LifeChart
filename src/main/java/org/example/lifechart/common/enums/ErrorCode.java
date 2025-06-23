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
	DELETED_USER_EXISTS(HttpStatus.BAD_REQUEST, "탈퇴 진행 중인 이메일입니다."),
	EXIST_SAME_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재 하는 닉네임입니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST,"입력한 값의 형식이 잘못되었습니다."),






















	// Auth (Line#: 50~79)
	NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
	NOT_EXIST_COOKIE(HttpStatus.NOT_FOUND, "쿠키가 존재하지 않습니다."),
	SC_BAD_REQUEST(HttpStatus.BAD_REQUEST, "JWT 토큰이 없거나 일치하지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다."),




	// jwt
	INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT 서명이 유효하지 않습니다."),
	MALFORMED_JWT(HttpStatus.UNAUTHORIZED, "JWT 형식이 올바르지 않습니다."),
	EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
	UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다."),
	EMPTY_JWT(HttpStatus.UNAUTHORIZED, "JWT 토큰이 비어 있습니다."),















	// Goal (Line#: 80~109)
	GOAL_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	GOAL_INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
	GOAL_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "잘못된 카테고리입니다."),
	GOAL_RETIREMENT_LIFESPAN_BEFORE_END_DATE(HttpStatus.BAD_REQUEST, "기대 수명은 목표 종료일 이후여야 합니다."),
	GOAL_LIFESPAN_DATA_NOT_EXIST(HttpStatus.UNPROCESSABLE_ENTITY, "성별과 연도에 해당하는 기대 수명 데이터가 존재하지 않습니다."),
	GOAL_INIT_DATA_MISSING(HttpStatus.INTERNAL_SERVER_ERROR, "목표 데이터 파일을 찾을 수 없습니다."),
	GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "목표가 존재하지 않습니다."),
	GOAL_HOUSING_NOT_FOUND(HttpStatus.NOT_FOUND, "주거 목표가 존재하지 않습니다."),
	GOAL_RETIREMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "은퇴 목표가 존재하지 않습니다."),
	GOAL_ETC_NOT_FOUND(HttpStatus.NOT_FOUND, "기타 목표가 존재하지 않습니다."),
	GOAL_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 목표입니다."),
	GOAL_CATEGORY_DETAIL_MISMATCH(HttpStatus.BAD_REQUEST, "카테고리와 카테고리 입력 필드가 일치하지 않습니다."),

















	// Simulation (Line#: 110~139)
	SIMULATION_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	SIMULATION_INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
	SIMULATION_NOT_FOUND(HttpStatus.NOT_FOUND, "시뮬레이션을 찾을 수 없습니다."),
	SIMULATION_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 목표가 존재하지 않습니다."),
	SIMULATION_PARAM_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "시뮬레이션 파라미터 유효성 검증에 실패했습니다."),
	SIMULATION_SAVE_FAILED(HttpStatus.BAD_REQUEST, "시뮬레이션 저장 중 오류가 발생했습니다."),
	SIMULATION_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "시뮬레이션 업데이트 중 오류가 발생했습니다."),
	SIMULATION_DELETE_FAILED(HttpStatus.BAD_REQUEST, "삭제할 수 없는 시뮬레이션입니다."),
	SIMULATION_LINKED_ENTITY_EXISTS(HttpStatus.BAD_REQUEST, "연결된 목표가 있는 시뮬레이션은 삭제할 수 없습니다."),
	INVALID_GOAL_CATEGORY(HttpStatus.BAD_REQUEST,"지원하지 않는 카테고리입니다."),



















	// Follow (Line#: 140~169)
	FOLLOW_CONFLICT(HttpStatus.CONFLICT, "팔로우가 이미 존재합니다."),
	FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우를 찾을 수 없습니다."),
	FOLLOW_FORBIDDEN(HttpStatus.FORBIDDEN, "본인을 팔로우 할 수 없습니다."),













	// shareGoal (Line#: 157~169)
	SHARE_GOAL_KEYWORD_BAD_REQEUST(HttpStatus.BAD_REQUEST, "키워드는 공백이면 안 되고 길이가 2이상이여야 합니다"),











	// Comment (Line#: 170~199)
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
	COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "댓글은 본인만 수정 및 삭제할 수 있습니다."),



























	// Like (Line#: 200~229)
	LIKE_CONFLICT(HttpStatus.CONFLICT, "좋아요를 이미 눌렀습니다."),
	LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 찾을 수 없습니다."),
	LIKE_FORBIDDEN(HttpStatus.FORBIDDEN, "좋아요 취소는 본인만 가능합니다."),


























	// Notification (Line#: 230~259)
	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
	NOTIFICATION_PERMISSION(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다.");

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
