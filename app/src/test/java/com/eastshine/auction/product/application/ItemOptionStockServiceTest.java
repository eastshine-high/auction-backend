package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.domain.item.option.ItemOptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemOptionStockServiceTest extends IntegrationTest {

    @Autowired
    ItemOptionStockService itemOptionStockService;
    @Autowired
    ItemOptionRepository itemOptionRepository;

    @Nested
    @DisplayName("decreaseStockWithLock 메소드는")
    class Describe_decreaseStockWithLock{

        @Test
        @DisplayName("3개의 재고의 동시 차감 요청을 처리할 수 있다.") // Github action 환경의 성능 문제로 테스트 동시 요청 최소화
        void decreaseStockWithLock() throws InterruptedException {
            int stockQuantity = 400;
            int concurrentConnectionCount = 3;
            ItemOption itemOption = ItemOption.builder()
                    .additionalPrice(9000)
                    .stockQuantity(stockQuantity)
                    .build();
            itemOptionRepository.save(itemOption);

            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(concurrentConnectionCount);

            for (int i = 0; i < concurrentConnectionCount; i++) {
                executorService.submit(() -> {
                    try {
                        itemOptionStockService.decreaseStockWithLock(itemOption.getId(), 1);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            ItemOption updatedItemOption = itemOptionRepository.findById(itemOption.getId()).orElseThrow();
            assertThat(updatedItemOption.getStockQuantity()).isEqualTo(stockQuantity - concurrentConnectionCount);
        }

        @Test
        @DisplayName("존재하지 않는 물품의 재고를 차감할 경우, EntityNotFoundException 예외를 던진다.")
        void decreaseStockWithNotExistId(){
            Long notExistId = 9999L;
            ItemOption itemOption = ItemOption.builder()
                    .additionalPrice(9000)
                    .stockQuantity(9999)
                    .build();
            itemOptionRepository.save(itemOption);

            assertThatThrownBy(() -> itemOptionStockService.decreaseStockWithLock(notExistId, 1))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage(ErrorCode.ITEM_NOT_FOUND.getErrorMsg());
        }
    }

    @Nested
    @DisplayName("increaseStockWithLock 메소드는")
    class Describe_increaseStockWithLock{

        @Test
        @DisplayName("3개의 재고의 동시 증가 요청을 처리할 수 있다.") // Github action 환경의 성능 문제로 테스트 동시 요청 최소화
        void increaseStockWithLock() throws InterruptedException {
            int stockQuantity = 400;
            int concurrentConnectionCount = 3;
            ItemOption itemOption = ItemOption.builder()
                    .additionalPrice(9000)
                    .stockQuantity(stockQuantity)
                    .build();
            itemOptionRepository.save(itemOption);

            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(concurrentConnectionCount);

            for (int i = 0; i < concurrentConnectionCount; i++) {
                executorService.submit(() -> {
                    try {
                        itemOptionStockService.increaseStockWithLock(itemOption.getId(), 1);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            ItemOption updatedItemOption = itemOptionRepository.findById(itemOption.getId()).orElseThrow();
            assertThat(updatedItemOption.getStockQuantity()).isEqualTo(stockQuantity + concurrentConnectionCount);
        }

        @Test
        @DisplayName("존재하지 않는 물품의 재고를 증가할 경우, EntityNotFoundException 예외를 던진다.")
        void increaseStockWithNotExistId() {
            Long notExistId = 9999L;
            ItemOption itemOption = ItemOption.builder()
                    .additionalPrice(9000)
                    .stockQuantity(9999)
                    .build();
            itemOptionRepository.save(itemOption);

            assertThatThrownBy(() -> itemOptionStockService.increaseStockWithLock(notExistId, 1))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage(ErrorCode.ITEM_NOT_FOUND.getErrorMsg());
        }
    }
}
