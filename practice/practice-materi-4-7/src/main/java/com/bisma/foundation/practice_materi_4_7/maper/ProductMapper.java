package com.bisma.foundation.practice_materi_4_7.maper;

import com.bisma.foundation.practice_materi_4_7.dto.ProductReqDto;
import com.bisma.foundation.practice_materi_4_7.dto.ProductResponseDto;
import com.bisma.foundation.practice_materi_4_7.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductReqDto productReqDto);
    ProductResponseDto toResponse(Product product);
    List<ProductResponseDto> toResponseList(List<Product> products);
}
