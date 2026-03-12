package com.bisma.foundation.practice_materi_4_7.entity;

import com.bisma.foundation.practice_materi_4_7.helper.StatusProduct;

import java.util.Date;

public class Product {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private StatusProduct status;
    private Long categoryId;
    private String createdBy;
    private Long authorId;
    private Date createdAt;
    private Date updatedAt;



    public Product() {
    }


}
