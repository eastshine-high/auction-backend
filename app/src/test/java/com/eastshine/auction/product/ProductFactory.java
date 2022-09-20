package com.eastshine.auction.product;

import com.eastshine.auction.product.application.SellerProductService;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.web.dto.SellerProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProductFactory {
    @Autowired SellerProductService sellerProductService;
    @Autowired ProductRepository productRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    public Product createProduct(Integer categoryId, String name) {
        return createProduct(categoryId, name, 5000, true, 5000);
    }

    public Product createProduct(Integer categoryId, String name, Integer price, Boolean onSale, Integer stockQuantity) {
        SellerProductDto.RegistrationRequest registrationRequest = SellerProductDto.RegistrationRequest.builder()
                .categoryId(categoryId)
                .name(name)
                .price(price)
                .onSale(onSale)
                .stockQuantity(stockQuantity)
                .productOptions(new ArrayList<>())
                .build();
        return sellerProductService.registerProduct(registrationRequest);
    }

    public void deleteAll() {
        productRepository.deleteAll();
        jdbcTemplate.execute("delete from product_option");
    }
}
