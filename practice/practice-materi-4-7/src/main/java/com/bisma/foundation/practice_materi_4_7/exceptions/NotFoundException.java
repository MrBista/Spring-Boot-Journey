package com.bisma.foundation.practice_materi_4_7.exceptions;

import com.bisma.foundation.practice_materi_4_7.ErrorCode;

public class NotFoundException extends RuntimeException{

    private ErrorCode errorCode = ErrorCode.NOT_FOUND;


    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
