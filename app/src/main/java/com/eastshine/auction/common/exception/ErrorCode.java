package com.eastshine.auction.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common
    COMMON_SYSTEM_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 장애 상황
    COMMON_INVALID_ARGUMENT("요청한 값이 올바르지 않습니다."),
    COMMON_ENTITY_NOT_FOUND("요청한 정보를 찾을 수 없습니다."),
    COMMON_ILLEGAL_STATUS("잘못된 상태값입니다."),
    COMMON_INVALID_TOKEN("유효하지 않은 토큰입니다."),
    COMMON_EXPIRED_TOKEN("유효 시간이 만료된 토큰입니다."),
    COMMON_UNAUTHORIZED_REQUEST("요청 권한이 없습니다."),
    COMMON_LOCK_FAIL("LOCK 을 수행하는 중에 오류가 발생하였습니다."),

    // user
    USER_DUPLICATE_EMAIL("이미 등록된 이메일입니다."),
    USER_DUPLICATE_NICKNAME("사용 중인 닉네임입니다."),
    USER_DUPLICATE_BUSINESS_NUMBER("등록된 사업자 번호입니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_LOGIN_FAIL("아이디 또는 비밀번호를 잘못 입력했습니다."),
    USER_INACCESSIBLE("사용자에 대한 접근 권한이 없습니다."),

    // product
    CATEGORY_PARENT_ENTITY_NOT_FOUND("상위 카테고리를 찾을 수 없습니다."),
    CATEGORY_ENTITY_NOT_FOUND("카테고리를 찾을 수 없습니다."),
    ITEM_NOT_FOUND("물품을 찾을 수 없습니다."),
    ITEM_OPTION_NOT_FOUND("물품 옵션을 찾을 수 없습니다."),
    ITEM_INACCESSIBLE("물품에 대한 접근 권한이 없습니다."),
    ITEM_STOCK_QUANTITY_ERROR("물품 재고를 확인해 주세요."),

    // order
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다."),
    ORDER_INACCESSIBLE("주문에 대한 접근 권한이 없습니다."),
    ORDER_ITEM_NOT_FOUND("주문 물품을 찾을 수 없습니다."),
    ORDER_ITEM_OPTION_NOT_FOUND("주문 물품 옵션을 찾을 수 없습니다."),
    ON_DELIVERY_ORDER("배송이 진행중인 주문은 취소할 수 없습니다."),
    NON_CANCELABLE_ORDER_STATE("주문을 취소할 수 없는 상태입니다.");
    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
