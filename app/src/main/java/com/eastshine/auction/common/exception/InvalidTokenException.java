package com.eastshine.auction.common.exception;

public class InvalidTokenException extends BaseException{
    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
