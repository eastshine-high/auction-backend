package com.eastshine.auction.order.domain;

import com.eastshine.auction.order.domain.item.OrderLine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void getTotalAmount() {
        Order order = new Order();
        List<OrderLine> orderLines = List.of(
                OrderLine.builder().itemPrice(5000).itemOptionPrice(600).orderCount(2).build(),
                OrderLine.builder().itemPrice(5000).itemOptionPrice(0).orderCount(1).build()
        );
        order.setOrderLines(orderLines);

        long totalAmount = order.getTotalAmount();

        assertThat(totalAmount).isEqualTo(
                ((5000 + 0) * 1) + ((5000 + 600) * 2)
        );
    }
}
