package com.eastshine.auction.common.exception;


public class IllegalStatusException extends BaseException {

    public IllegalStatusException() {
        super(ErrorCode.COMMON_ILLEGAL_STATUS);
    }

    public IllegalStatusException(ErrorCode errorCode) {
        super(errorCode);
    }

    public IllegalStatusException(String message) {
        super(message, ErrorCode.COMMON_ILLEGAL_STATUS);
    }
}
