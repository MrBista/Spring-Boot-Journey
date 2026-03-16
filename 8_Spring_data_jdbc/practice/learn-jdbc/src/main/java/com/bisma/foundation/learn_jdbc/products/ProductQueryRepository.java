package com.bisma.foundation.learn_jdbc.products;

import java.util.List;
import java.util.Optional;

public interface ProductQueryRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product saveProduct(Product product);
    void update(Product product);
    void deleteById(Long id);
    void delete(Product product);
}
