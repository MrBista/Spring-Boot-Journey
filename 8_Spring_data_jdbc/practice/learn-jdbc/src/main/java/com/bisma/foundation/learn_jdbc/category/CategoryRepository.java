package com.bisma.foundation.learn_jdbc.category;

import com.bisma.foundation.learn_jdbc.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository extends ListCrudRepository<Category, Long>,
        PagingAndSortingRepository<Category, Long> {

    Page<Category> findByName(String name, Pageable page);

}
