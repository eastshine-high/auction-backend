package com.eastshine.auction.common.exception;

public class UnauthorizedException extends BaseException{

    public UnauthorizedException() {
        super(ErrorCode.COMMON_UNAUTHORIZED_REQUEST);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
