package com.bisma.foundation.learn_jdbc.products;

import static org.junit.jupiter.api.Assertions.*;

import com.bisma.foundation.learn_jdbc.exception.BadRequest;
import com.bisma.foundation.learn_jdbc.exception.ResourceNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
// Gunakan anotasi di bawah jika kamu ingin pakai database asli (MySQL/Postgre) saat test,
// bukan database in-memory:
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void testProductRepository() {
        List<Product> products = productRepository.findAll();
        assertNotNull(products);

        System.out.println(Arrays.toString(products.toArray()));


    }

    @Test
    void testProductFindById() {
        Product product = productRepository
                .findById(1L)
                .orElseThrow(() -> new ResourceNotFound("product not found"));

        assertNotNull(product);
        System.out.println(product.toString());
    }


    @Test
//    @Transactional
    void testSaveProduct() {
        Product product = new Product();
        product.setName("barabara");
        product.setSku("buruburu");
        product.setDescription("bibib");
        product.setStatus(1L);
        product.setPrice(1000_0.00);

        Product productInserted = productRepository.save(product);
        assertNotNull(productInserted);
        assertNotNull(productInserted.getId());
        System.out.println("data inserted " + productInserted.toString());
    }


    @Test
    void testDeleteByIdProduct() {
        Long idToDeleted = 1L;
        Product product = productRepository.findById(idToDeleted).orElseThrow(() -> new ResourceNotFound("product not found"));

        productRepository.deleteById(product.getId());
    }

    @Test
    void testSearchByName() {
        Product product = productRepository.findByName("barabara");

        System.out.println(product.toString());
    }

    @Test
    void testPageable() {
        Pageable page = PageRequest.of(1, 10);

        Page<Product> pageProduct = productRepository.findAll(page);

        assertNotNull(pageProduct);
        System.out.println(Arrays.toString(pageProduct.stream().toArray()));
        System.out.println("total elements: " + pageProduct.getTotalElements());
        System.out.println(Arrays.toString(pageProduct.get().toArray()));

        pageProduct.stream().forEach(System.out::println);
    }



}
