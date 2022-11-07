package com.eastshine.auction.order.domain.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {


    @Nested
    @DisplayName("calculateTotalAmount 메소드는")
    class Describe_calculateTotalAmount{

        @Test
        @DisplayName("orderItemOptions가 없는 경우 주문 아이템의 총 가격을 구한다.")
        void Context_with_not_exist_orderItemOptions() {
            OrderItem orderItem = OrderItem.builder().itemPrice(6000).orderCount(2).build();

            long actual = orderItem.calculateTotalAmount();

            assertThat(actual).isEqualTo(6000 * 2);
        }

        @Test
        @DisplayName("orderItemOptions가 있는 경우, 아이템 옵션의 총합을 구한다.")
        void Context_with_exist_orderItemOptions() {
            OrderItem orderItem = OrderItem.builder().itemPrice(6000).orderCount(3).build();
            OrderItemOption orderItemOption = OrderItemOption.builder()
                    .additionalPrice(300)
                    .orderCount(2).build();
            orderItem.addOrderItemOption(orderItemOption);

            long actual = orderItem.calculateTotalAmount();

            assertThat(actual).isEqualTo((6000 + 300) * 2);
            assertThat(actual).isNotEqualTo(6000 * 3);
        }
    }
}
