package com.bisma.foundation.learn_jdbc.products;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends ListCrudRepository<Product, Long> {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    @Modifying
    Product save(Product product);
    void update(Product product);
    void deleteById(Long id);
    void delete(Product product);


}
