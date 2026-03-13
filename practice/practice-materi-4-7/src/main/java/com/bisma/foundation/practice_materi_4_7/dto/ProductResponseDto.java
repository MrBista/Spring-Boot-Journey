package com.bisma.foundation.practice_materi_4_7.dto;

import com.bisma.foundation.practice_materi_4_7.helper.StatusProduct;

public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private StatusProduct status;
    private Long categoryId;
    private String createdBy;
    private Long authorId;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String name, String description, String sku, StatusProduct status, Long categoryId, String createdBy, Long authorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.status = status;
        this.categoryId = categoryId;
        this.createdBy = createdBy;
        this.authorId = authorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StatusProduct getStatus() {
        return status;
    }

    public void setStatus(StatusProduct status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "ProductResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sku='" + sku + '\'' +
                ", status=" + status +
                ", categoryId=" + categoryId +
                ", createdBy='" + createdBy + '\'' +
                ", authorId=" + authorId +
                '}';
    }
}
