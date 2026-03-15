package com.bisma.foundation.learn_jdbc.products;

import java.util.List;

public interface ProductRepository {
    List<Product> findAll();
    Product findById(Long id);
    void save(Product product);
    void update(Product product);
    void deleteById(Long id);
    void delete(Product product);
}
