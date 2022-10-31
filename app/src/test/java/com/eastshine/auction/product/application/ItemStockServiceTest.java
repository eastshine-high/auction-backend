package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemStockServiceTest extends IntegrationTest {

    @Autowired
    ItemStockService itemStockService;
    @Autowired
    ItemRepository itemRepository;

    @Nested
    @DisplayName("decreaseStockWithLock 메소드는")
    class Describe_decreaseStock{

        @Test
        @DisplayName("5개의 재고 동시 차감 요청을 처리할 수 있다.") // Github action 환경의 성능 문제로 테스트 동시 요청 최소화
        void decreaseStockWithLock() throws InterruptedException {
            int stockQuantity = 500;
            int concurrentConnectionCount = 5;
            Item item = Item.builder()
                    .stockQuantity(stockQuantity)
                    .categoryId(500)
                    .name("김치")
                    .onSale(true)
                    .price(30000)
                    .build();
            itemRepository.save(item);

            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(concurrentConnectionCount);

            for (int i = 0; i < concurrentConnectionCount; i++) {
                executorService.submit(() -> {
                    try {
                        itemStockService.decreaseStockWithLock(item.getId(), 1);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
            assertThat(updatedItem.getStockQuantity()).isEqualTo(stockQuantity - concurrentConnectionCount);
        }

        @Test
        @DisplayName("존재하지 않는 물품의 재고를 차감할 경우, EntityNotFoundException 예외를 던진다.")
        void decreaseStockWithNotExistId() throws InterruptedException {
            Long notExistId = 9999L;
            Item item = Item.builder()
                    .stockQuantity(500)
                    .categoryId(500)
                    .name("김치")
                    .onSale(true)
                    .price(30000)
                    .build();
            itemRepository.save(item);

            assertThatThrownBy(() -> itemStockService.decreaseStockWithLock(notExistId, 1))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage(ErrorCode.ITEM_NOT_FOUND.getErrorMsg());
        }
    }
}
