package com.eastshine.auction.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    COMMON_SYSTEM_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 장애 상황
    COMMON_INVALID_ARGUMENT("요청한 값이 올바르지 않습니다."),
    COMMON_ENTITY_NOT_FOUND("요청한 정보를 찾을 수 없습니다."),
    COMMON_ILLEGAL_STATUS("잘못된 상태값입니다."),
    COMMON_INVALID_TOKEN("유효하지 않은 토큰입니다."),
    COMMON_UNAUTHORIZED_REQUEST("요청 권한이 없습니다."),

    // Category
    CATEGORY_PARENT_ENTITY_NOT_FOUND("상위 카테고리를 찾을 수 없습니다."),

    // Product
    PRODUCT_INVALID_CATEGORY_ID("유효하지 않은 카테고리 ID입니다."),

    // User
    USER_DUPLICATE_EMAIL("이미 등록된 이메일입니다."),
    USER_DUPLICATE_NICKNAME("사용 중인 닉네임입니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_LOGIN_FAIL("아이디 또는 비밀번호를 잘못 입력했습니다.");

    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
