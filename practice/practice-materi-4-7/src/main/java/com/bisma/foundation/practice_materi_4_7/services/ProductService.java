package com.bisma.foundation.practice_materi_4_7.services;

import com.bisma.foundation.practice_materi_4_7.dto.ProductReqDto;
import com.bisma.foundation.practice_materi_4_7.dto.ProductResponseDto;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductResponseDto createProduct(ProductReqDto productReqDto);

    void updateProduct(ProductReqDto productReqDto, Long id);

    void deleteProductById(Long id);

    Optional<ProductResponseDto> findProductById(Long id);

    List<ProductResponseDto> findAllProduct();


}
