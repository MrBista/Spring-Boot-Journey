package com.bisma.foundation.learn_jdbc.category;

import com.bisma.foundation.learn_jdbc.helper.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCategory() {
        return ResponseEntity.ok(ApiResponse.of(categoryService.getAllCategory()));
    }
}
