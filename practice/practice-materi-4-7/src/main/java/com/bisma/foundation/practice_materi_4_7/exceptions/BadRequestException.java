package com.bisma.foundation.practice_materi_4_7.exceptions;


import com.bisma.foundation.practice_materi_4_7.ErrorCode;

public class BadRequestException extends RuntimeException{

    private  ErrorCode errorCode;

    public BadRequestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BadRequestException(String message) {
        super(message);
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
