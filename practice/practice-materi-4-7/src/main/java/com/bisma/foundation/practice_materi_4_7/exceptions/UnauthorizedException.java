package com.bisma.foundation.practice_materi_4_7.exceptions;

import com.bisma.foundation.practice_materi_4_7.ErrorCode;

public class UnauthorizedException extends RuntimeException{

    private ErrorCode errorCode;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
