package com.eastshine.auction.product.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.web.dto.SystemProductDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"dev"})
class SystemProductControllerTest extends WebIntegrationTest {

    @Autowired SystemProductController systemProductController;
    @Autowired ProductRepository productRepository;
    @Autowired CategoryFactory categoryFactory;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        categoryFactory.deleteAll();
    }

    // @Test
    @EnabledOnOs(OS.MAC)
    public void decreaseStock() throws Exception {
        categoryFactory.createCategory(500, "식품");
        int stockQuantity = 6;
        Product product = Product.builder()
                .stockQuantity(stockQuantity)
                .categoryId(500)
                .name("김치")
                .onSale(true)
                .price(30000)
                .build();
        productRepository.save(product);

        SystemProductDto.DecreaseStock decreaseStock = new SystemProductDto.DecreaseStock(
                List.of(new SystemProductDto.DecreaseStock.Product(product.getId(), 1,
                        List.of(new SystemProductDto.DecreaseStock.ProductOption(null, 0))
                ))
        );

        mockMvc.perform(
                post("/system-api/products/decrease-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson(decreaseStock))
        )
                .andExpect(status().isOk());
    }
}
