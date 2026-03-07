package com.bisma.foundation.fundamental_spring_boot.Enum;

public enum ErrorCode {
    BAD_REQUEST(400, "Terjadi keslaahan");

    private final int code;
    private final String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
