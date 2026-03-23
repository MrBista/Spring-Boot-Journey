package com.bisma.foundation.learn_jdbc.helper;


public class ApiResponse <T>{

    private T data;
    private String message;
    private boolean success;

    public static <T> ApiResponse<T> of(T t) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(t);
        response.setSuccess(true);

        return response;
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
}
