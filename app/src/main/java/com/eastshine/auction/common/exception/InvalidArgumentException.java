package com.eastshine.auction.common.exception;

public class InvalidArgumentException extends BaseException {

    public InvalidArgumentException() {
        super(ErrorCode.COMMON_INVALID_ARGUMENT);
    }

    public InvalidArgumentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidArgumentException(String errorMsg) {
        super(errorMsg, ErrorCode.COMMON_INVALID_ARGUMENT);
    }

    public InvalidArgumentException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
