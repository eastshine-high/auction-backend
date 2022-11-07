package com.eastshine.auction.order.domain.item;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemOptionTest {

    @Test
    void calculateTotalAmount() {
        OrderItemOption orderItemOption = OrderItemOption.builder()
                .additionalPrice(300)
                .orderCount(2).build();
        orderItemOption.setOrderItem(OrderItem.builder().itemPrice(6000).build());

        long actual = orderItemOption.calculateTotalAmount();

        assertThat(actual).isEqualTo((6000 + 300) * 2);
    }
}
