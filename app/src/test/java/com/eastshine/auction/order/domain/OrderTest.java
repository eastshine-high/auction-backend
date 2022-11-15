package com.eastshine.auction.order.domain;

import com.eastshine.auction.order.domain.item.OrderItem;
import com.eastshine.auction.order.domain.item.OrderItemOption;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void calculateTotalAmount() {
        // given
        Order order = new Order();

        OrderItem orderItem1 = OrderItem.builder().itemPrice(6000).orderCount(3).build();
        ReflectionTestUtils.setField(orderItem1, "id", 1L);
        order.addOrderItem(orderItem1);

        OrderItem orderItem2 = OrderItem.builder().itemPrice(7000).build();
        ReflectionTestUtils.setField(orderItem2, "id", 2L);
        OrderItemOption orderItemOption2_1 = OrderItemOption.builder()
                .additionalPrice(300)
                .orderCount(2)
                .build();
        orderItem2.addOrderItemOption(orderItemOption2_1);
        order.addOrderItem(orderItem2);

        // when
        long actual = order.calculateTotalAmount();

        // then
        assertThat(actual).isEqualTo((6000 * 3) + ((7000 + 300) * 2));
    }

    @Test
    void changeCanceledStatus() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "orderStatus", Order.OrderStatus.INIT);

        order.changeCanceledStatus();

        assertThat(order.getOrderStatus()).isEqualTo(Order.OrderStatus.CANCELED);
    }
}
