package com.bisma.foundation.practice_materi_4_7.services;

import com.bisma.foundation.practice_materi_4_7.dto.ProductReqDto;
import com.bisma.foundation.practice_materi_4_7.dto.ProductResponseDto;
import com.bisma.foundation.practice_materi_4_7.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {
    @Override
    public ProductResponseDto createProduct(ProductReqDto productReqDto) {
        return null;
    }

    @Override
    public void updateProduct(ProductReqDto productReqDto) {

    }

    @Override
    public void deleteProductById(Long id) {

    }

    @Override
    public Optional<ProductResponseDto> findProductById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ProductResponseDto> findAllProduct() {
        return List.of();
    }
}
