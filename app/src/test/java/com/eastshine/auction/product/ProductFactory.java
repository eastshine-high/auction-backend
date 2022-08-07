package com.eastshine.auction.product;

import com.eastshine.auction.product.application.SellerProductService;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.domain.ProductRepository;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory {
    @Autowired SellerProductService sellerProductService;
    @Autowired ProductRepository productRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    public Product createProduct(Integer categoryId, String name) {
        return createProduct(categoryId, name, 5000, true);
    }

    public Product createProduct(Integer categoryId, String name, Integer price, Boolean onSale) {
        SellerProductRegistrationRequest registrationRequest = SellerProductRegistrationRequest.builder()
                .categoryId(categoryId)
                .name(name)
                .price(price)
                .onSale(onSale)
                .build();
        return sellerProductService.registerProduct(registrationRequest);
    }

    public void deleteAll() {
        productRepository.deleteAll();
        jdbcTemplate.execute("delete from product_option");
    }
}
