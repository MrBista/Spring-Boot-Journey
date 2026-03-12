package com.bisma.foundation.practice_materi_4_7.repository;

import com.bisma.foundation.practice_materi_4_7.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository{
    @Override
    public Product findById(Long id) {
        return null;
    }

    @Override
    public List<Product> findAllProduct() {
        return List.of();
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public void update(Product product) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
