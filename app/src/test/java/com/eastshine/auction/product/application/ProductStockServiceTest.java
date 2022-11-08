package com.eastshine.auction.product.application;

import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.order.web.dto.OrderDto;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.domain.item.option.ItemOptionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductStockServiceTest extends IntegrationTest {
    private static final int STOCK_QUANTITY = 50;

    private static Item item;
    private static ItemOption itemOption;

    @Autowired ProductStockService productStockService;
    @Autowired ItemRepository itemRepository;
    @Autowired ItemOptionRepository itemOptionRepository;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .stockQuantity(STOCK_QUANTITY)
                .categoryId(500)
                .name("김치")
                .onSale(true)
                .price(30000)
                .build();
        itemRepository.save(item);

        itemOption = ItemOption.builder()
                .item(item)
                .additionalPrice(9000)
                .stockQuantity(STOCK_QUANTITY)
                .build();
        itemOptionRepository.save(itemOption);
    }

    @AfterEach
    void tearDown() {
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Nested
    @DisplayName("decreaseStock 메소드는")
    class Describe_decreaseStock{

        @Test
        @DisplayName("요청 아규먼트의 itemOption 리스트가 비어있을 경우, Item의 재고를 차감한다.")
        void decreaseStockWithEmptyItemOptionId() {
            int orderCount = 3;
            OrderDto.PlaceOrderItem orderItem = new OrderDto.PlaceOrderItem(item.getId(), orderCount,null);

            productStockService.decreaseStock(orderItem);

            Item actualItem = itemRepository.findById(ProductStockServiceTest.item.getId()).get();
            ItemOption actualItemOption = itemOptionRepository.findById(ProductStockServiceTest.itemOption.getId()).get();

            assertThat(actualItem.getStockQuantity()).isEqualTo(STOCK_QUANTITY - orderCount);
            assertThat(actualItemOption.getStockQuantity()).isEqualTo(STOCK_QUANTITY);
            assertThat(actualItemOption.getStockQuantity()).isNotEqualTo(STOCK_QUANTITY - orderCount);
        }

        @Test
        @DisplayName("요청 아규먼트의 itemOption 리스트가 있을 경우, ItemOption의 재고를 차감한다.")
        void decreaseStockWithExistItemOptionId() {
            int orderCount = 3;
            OrderDto.PlaceOrderItem orderItem = new OrderDto.PlaceOrderItem(item.getId(), 0,
                    List.of(new OrderDto.PlaceOrderItemOption(itemOption.getId(), orderCount)));

            productStockService.decreaseStock(orderItem);

            Item actualItem = itemRepository.findById(ProductStockServiceTest.item.getId()).get();
            ItemOption actualItemOption = itemOptionRepository.findById(ProductStockServiceTest.itemOption.getId()).get();

            assertThat(actualItemOption.getStockQuantity()).isEqualTo(STOCK_QUANTITY - orderCount);
            assertThat(actualItem.getStockQuantity()).isEqualTo(STOCK_QUANTITY);
            assertThat(actualItem.getStockQuantity()).isNotEqualTo(STOCK_QUANTITY - orderCount);
        }
    }
}
