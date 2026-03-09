package com.bisma.foundation.practice_materi_4_7;

public enum ErrorCode {
    BAD_REQUEST(400, "Terjadi kesalahan"),
    NOT_FOUND(404, "Tidak ditemukan");

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
