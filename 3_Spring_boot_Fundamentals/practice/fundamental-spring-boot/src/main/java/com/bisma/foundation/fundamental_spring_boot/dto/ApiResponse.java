package com.bisma.foundation.fundamental_spring_boot.dto;

import com.bisma.foundation.fundamental_spring_boot.Enum.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private T data;
    private String message;
    private boolean success;
    private ErrorCode errorCode;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setData(data);
        res.setSuccess(true);
        return res;
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setMessage(errorCode.getDesc());
        res.setErrorCode(errorCode);
        res.setSuccess(false);
        return res;
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setMessage(message);
        res.setErrorCode(errorCode);
        res.setSuccess(false);
        return res;
    }
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
