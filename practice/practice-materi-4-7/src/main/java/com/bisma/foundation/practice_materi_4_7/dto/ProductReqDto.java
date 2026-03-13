package com.bisma.foundation.practice_materi_4_7.dto;

public class ProductReqDto {
    private String name;
    private String description;
    private String sku;
    private Long categoryId;

    public ProductReqDto() {
    }

    public ProductReqDto(String name, String description, String sku, Long categoryId) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ProductReqDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sku='" + sku + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }
}
