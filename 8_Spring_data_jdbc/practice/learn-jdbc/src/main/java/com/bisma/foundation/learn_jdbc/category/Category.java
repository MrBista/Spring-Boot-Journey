package com.bisma.foundation.learn_jdbc.category;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("categories")
public class Category {

    @Id
    private Long category;
    private String name;
    private String description;
    private int status;

    public Category() {
    }

    public Category(Long category, String name, String description, int status) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Category(Long category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Category{" +
                "category=" + category +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
