package com.eastshine.auction.product.application;

import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.web.dto.SystemProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"dev"})
class ProductStockServiceTest extends IntegrationTest {

    @Autowired ProductStockService productStockService;
    @Autowired ProductRepository productRepository;
    @Autowired CategoryFactory categoryFactory;

    @BeforeEach
    void tearDown() {
        productRepository.deleteAll();
        categoryFactory.deleteAll();
    }

    // @Test
    @EnabledOnOs(OS.MAC)
    public void 동시에_3명이_주문() throws InterruptedException {
        categoryFactory.createCategory(500, "식품");

        int threadCount = 3;
        Product product = Product.builder()
                .stockQuantity(threadCount)
                .categoryId(500)
                .name("김치")
                .onSale(true)
                .price(30000)
                .build();
        productRepository.save(product);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        SystemProductDto.DecreaseStock request = new SystemProductDto.DecreaseStock(
                List.of(new SystemProductDto.DecreaseStock.Product(product.getId(), 1, null))
        );

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productStockService.decreaseStock(request);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(0);
    }
}
