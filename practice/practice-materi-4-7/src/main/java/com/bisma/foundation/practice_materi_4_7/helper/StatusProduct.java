package com.bisma.foundation.practice_materi_4_7.helper;

public enum StatusProduct {
    AKTIF(1, "STATUS PRODUCT AKTIF"),
    NON_AKTIF(0, "STATUS PRODUCT NON AKTIF");

    private final int code;
    private final String description;

    StatusProduct(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static StatusProduct fromCode(int code) {
        for (StatusProduct s : StatusProduct.values()) {
            if (s.code == code) return s;

        }
        throw new IllegalArgumentException("Unkwon code enum product for code: " + code);
    }
}
