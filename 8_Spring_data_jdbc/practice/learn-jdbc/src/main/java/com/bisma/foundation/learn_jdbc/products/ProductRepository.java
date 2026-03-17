package com.bisma.foundation.learn_jdbc.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends ListCrudRepository<Product, Long> {
    Page<Product> findAll(Pageable page);
    Optional<Product> findById(Long id);
    @Modifying
    Product save(Product product);
    void update(Product product);
    void delete(Product product);

    Product findByName(String name);


}
