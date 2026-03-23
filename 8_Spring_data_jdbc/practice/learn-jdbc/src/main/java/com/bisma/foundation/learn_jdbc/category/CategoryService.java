package com.bisma.foundation.learn_jdbc.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<Category> getAllCategory() {
        int defaultSize = 10;
        int pageNumber = 1;

        return categoryRepository.findAll(PageRequest.of(pageNumber, defaultSize));
    }
}
