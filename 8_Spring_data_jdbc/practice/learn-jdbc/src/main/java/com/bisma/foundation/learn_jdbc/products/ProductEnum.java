package com.bisma.foundation.learn_jdbc.products;

import com.bisma.foundation.learn_jdbc.exception.BadRequest;

import java.util.Arrays;

public enum ProductEnum {
    AKTIF(1, "PRODUCT PUBLISH"),
    NON_AKTIF(0, "PRODUCT NON AKTIF"),
    BANNED(2, "PRODUCT BANNED");

    private final int code;
    private final String desc;

    ProductEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ProductEnum toCode(int code) {
        return Arrays
                .stream(ProductEnum.values())
                .filter((val) -> val.code != code)
                .findFirst()
                .orElseThrow(() -> new BadRequest("Invalid enum type"));
    }
}
