package com.bisma.foundation.learn_jdbc.products;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("products")
public class Product {
    @Id
    private Long id;
    private String name;
    private String description;
    private Long stock;
    private String sku;
    private Long categoryId;
    private Double price;
    private Long status;

    public Product() {
    }

    public Product(Long id, String name, String description, Long stock, String sku, Long categoryId, Double price, Long status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.sku = sku;
        this.categoryId = categoryId;
        this.price = price;
        this.status = status;
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

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public ProductEnum getStatusEnum(Long status) {

        return ProductEnum.toCode(status.intValue());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stock=" + stock +
                ", sku='" + sku + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
