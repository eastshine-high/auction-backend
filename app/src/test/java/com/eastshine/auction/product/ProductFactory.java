package com.eastshine.auction.product;

import com.eastshine.auction.product.application.ProductService;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.domain.ProductRepository;
import com.eastshine.auction.product.domain.category.ProductCategoryRepository;
import com.eastshine.auction.product.web.dto.ProductRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory {
    @Autowired ProductService productService;
    @Autowired ProductRepository productRepository;
    @Autowired ProductCategoryRepository productCategoryRepository;

    public Product createProduct(Integer categoryId, String name) {
        return createProduct(categoryId, name, 5000, true);
    }

    public Product createProduct(Integer categoryId, String name, Integer price, Boolean onSale) {
        ProductRegistrationRequest registrationRequest = ProductRegistrationRequest.builder()
                .categoryId(categoryId)
                .name(name)
                .price(price)
                .onSale(onSale)
                .build();
        return productService.registerProduct(registrationRequest);
    }

    public void deleteAll() {
        productCategoryRepository.deleteAll();
        productRepository.deleteAll();
    }
}
