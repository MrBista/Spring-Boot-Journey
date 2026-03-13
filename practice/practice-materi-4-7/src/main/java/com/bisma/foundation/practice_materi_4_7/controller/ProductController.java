package com.bisma.foundation.practice_materi_4_7.controller;

import com.bisma.foundation.practice_materi_4_7.dto.ApiResponse;
import com.bisma.foundation.practice_materi_4_7.dto.ProductReqDto;
import com.bisma.foundation.practice_materi_4_7.dto.ProductResponseDto;
import com.bisma.foundation.practice_materi_4_7.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> findAllProduct() {
        return ResponseEntity
                .ok(ApiResponse.success(productService.findAllProduct()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@Valid @RequestBody ProductReqDto productReqDto) {
        return ResponseEntity
                .ok(ApiResponse.success(productService.createProduct(productReqDto)));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> findById(@PathVariable Long id) {
        return ResponseEntity
                .ok(ApiResponse.success(productService.findProductById(id).orElse(null)));
    }


    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Boolean>> updateByIdProduct(@Valid @RequestBody ProductReqDto productReqDto, @PathVariable Long id) {
        productService.updateProduct(productReqDto, id);

        return ResponseEntity
                .ok(ApiResponse.success(true));
    }


    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteById(@PathVariable Long id) {
        productService.deleteProductById(id);

        return ResponseEntity
                .ok(ApiResponse.success(true));
    }




}
