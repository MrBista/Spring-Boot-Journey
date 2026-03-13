package com.bisma.foundation.practice_materi_4_7.repository;

import com.bisma.foundation.practice_materi_4_7.ErrorCode;
import com.bisma.foundation.practice_materi_4_7.entity.Product;
import com.bisma.foundation.practice_materi_4_7.entity.User;
import com.bisma.foundation.practice_materi_4_7.exceptions.NotFoundException;
import com.bisma.foundation.practice_materi_4_7.helper.StatusProduct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository{

    private List<Product> products = new ArrayList<>(generateUsers(10));

    private static Product product(int index) {
        Product product = new Product();

        product.setId((long) index);
        product.setName("Product " + index);
        product.setDescription("Description for product " + index);
        product.setSku("SKU-" + String.format("%03d", index));
        product.setStatus(StatusProduct.values()[index % StatusProduct.values().length]);
        product.setCategoryId((long) (index % 5) + 1);
        product.setCreatedBy("admin");
        product.setAuthorId((long) (index % 3) + 1);
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());

        return product;
    }

    private static List<Product> generateUsers(int amountUsers) {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < amountUsers; i++) {
            // Oper nilai i ke method user()
            productList.add(product(i));
        }
        return productList;
    }

    @Override
    public Product findById(Long id) {
        
        return products
                .stream()
                .filter((val) -> val.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Products not found", ErrorCode.NOT_FOUND));
    }

    @Override
    public List<Product> findAllProduct() {
        return products;
    }

    @Override
    public Product save(Product product) {
        Long lastId = products.get(products.size() - 1).getId();

        product.setId(lastId + 1);

        products.add(product);

        return product;
    }

    @Override
    public void update(Product product) {

        products = products.stream().map((val) -> {
            if (val.getId().equals(product.getId())) {
                return product;
            }
            return val;
        }).toList();
    }

    @Override
    public void deleteById(Long id) {
        products = products
                .stream()
                .filter(val -> !val.getId().equals(id))
                .toList();
    }
}
