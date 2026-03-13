package com.bisma.foundation.practice_materi_4_7.services;

import com.bisma.foundation.practice_materi_4_7.dto.ProductReqDto;
import com.bisma.foundation.practice_materi_4_7.dto.ProductResponseDto;
import com.bisma.foundation.practice_materi_4_7.entity.Product;
import com.bisma.foundation.practice_materi_4_7.helper.StatusProduct;
import com.bisma.foundation.practice_materi_4_7.maper.ProductMapper;
import com.bisma.foundation.practice_materi_4_7.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponseDto createProduct(ProductReqDto productReqDto) {

        Product product = productMapper.toEntity(productReqDto);

        product.setStatus(StatusProduct.AKTIF);
        product.setCreatedBy("BismaBratha@mail.com");
        product.setCreatedAt(new Date(System.currentTimeMillis()));
        product.setAuthorId(1L);
        product.setSku(product.getName() + UUID.randomUUID().toString());


        Product productCreated = productRepository.save(product);



        return productMapper
                .toResponse(productCreated);
    }

    @Override
    public void updateProduct(ProductReqDto productReqDto, Long id) {
        productRepository
                .findById(id);

        Product product = productMapper
                .toEntity(productReqDto);
        product.setId(id);

        productRepository
                .update(product);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id);
        productRepository.deleteById(id);
    }

    @Override
    public Optional<ProductResponseDto> findProductById(Long id) {
        Product findById = productRepository.findById(id);

        return Optional.of(productMapper.toResponse(findById));
    }

    @Override
    public List<ProductResponseDto> findAllProduct() {
        return productMapper.toResponseList(productRepository.findAllProduct());
    }
}
