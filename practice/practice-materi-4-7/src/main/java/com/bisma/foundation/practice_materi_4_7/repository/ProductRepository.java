package com.bisma.foundation.practice_materi_4_7.repository;

import com.bisma.foundation.practice_materi_4_7.entity.Product;

import java.util.List;

public interface ProductRepository {
    Product findById(Long id);
    List<Product> findAllProduct();
    Product save(Product product);
    void update(Product product);
    void deleteById(Long id);
}
