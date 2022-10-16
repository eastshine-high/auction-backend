package com.eastshine.auction.product.application;

import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.web.dto.SystemItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"dev"})
class ItemStockServiceTest extends IntegrationTest {

    @Autowired ItemStockService itemStockService;
    @Autowired ItemRepository itemRepository;
    @Autowired CategoryFactory categoryFactory;

    @BeforeEach
    void tearDown() {
        itemRepository.deleteAll();
        categoryFactory.deleteAll();
    }

    // @Test - 공개 리포지토리에서 테스트 불가
    public void 동시에_3명이_주문() throws InterruptedException {
        categoryFactory.createCategory(500, "식품");

        int threadCount = 3;
        Item item = Item.builder()
                .stockQuantity(threadCount)
                .categoryId(500)
                .name("김치")
                .onSale(true)
                .price(30000)
                .build();
        itemRepository.save(item);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        SystemItemDto.DecreaseStock request = new SystemItemDto.DecreaseStock(
                List.of(new SystemItemDto.DecreaseStock.Item(item.getId(), 1, null))
        );

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    itemStockService.decreaseStock(request);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getStockQuantity()).isEqualTo(0);
    }
}
