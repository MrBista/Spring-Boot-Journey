package com.bisma.foundation.learn_jdbc.products;

import com.bisma.foundation.learn_jdbc.exception.BadRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class ProductRepositoryImpl implements ProductRepository{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ProductRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private Product mapToProduct(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setSku(rs.getString("sku"));
        product.setCategoryId(rs.getLong("category_id"));
        product.setStatus(rs.getLong("status"));
        return product;
    }

    @Override
    public List<Product> findAll() {
        String sql = """
                select id, name, description, stock, sku, category_id, price, status from products;
                """;

        return namedParameterJdbcTemplate.query(sql, this::mapToProduct) ;
    }

    @Override
    public Product findById(Long id) {
        String sql = """
                select id, name, description, stock, sku, category_id, price, status\s
                from products
                where id = :id;
               \s""";

        return namedParameterJdbcTemplate
                .queryForObject(sql, new MapSqlParameterSource("id", id), this::mapToProduct)
                ;
    }

    @Override
    public void save(Product product) {
        String sql = """
                insert into products (name, description, stock, sku, category_id, price, status)\s
                values(:name, :description, :stock, :sku, :categoryId, :price, :status)
               \s""";

        MapSqlParameterSource productMap = new MapSqlParameterSource();
        productMap.addValue("name", product.getName());
        productMap.addValue("description", product.getDescription());
        productMap.addValue("stock", product.getStock());
        productMap.addValue("sku", product.getSku());
        productMap.addValue("categoryId", product.getCategoryId());
        productMap.addValue("price", product.getPrice());
        productMap.addValue("status", product.getStatus());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, productMap, keyHolder);

        var keyHolderVal = keyHolder.getKeyAs(Product.class);
        if (keyHolderVal == null) {
            throw new BadRequest("no value inserted to db");
        }
        product.setId(keyHolderVal.getId());
    }

    @Override
    public void update(Product product) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(Product product) {

    }
}
