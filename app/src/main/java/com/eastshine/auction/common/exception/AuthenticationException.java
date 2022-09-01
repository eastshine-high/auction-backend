package com.eastshine.auction.common.exception;

public class AuthenticationException extends BaseException{
    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
