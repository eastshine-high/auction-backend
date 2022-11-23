package com.eastshine.auction.order.domain;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.order.domain.item.OrderItem;
import com.eastshine.auction.order.domain.item.OrderItemOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Nested
    @DisplayName("validateAccessibleUser 메소드는")
    class Describe_validateAccessibleUser{

        @Test
        @DisplayName("주문한 사용자가 아닐 경우, UnauthorizedException 예외를 던진다.")
        void contextWithInaccessibleUser() {
            Order order = Order.builder().userId(1L).build();

            assertThatThrownBy(() -> order.validateAccessibleUser(999999L))
                    .isExactlyInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorCode.ORDER_INACCESSIBLE.getErrorMsg());
        }

        @Test
        @DisplayName("주문한 사용자일 경우, 예외를 던지지 않는다.")
        void contextWithAccessibleUser() {
            Long orderUserId = 3L;
            Order order = Order.builder().userId(orderUserId).build();

            order.validateAccessibleUser(orderUserId);
        }
    }

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
    void cancel() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "orderStatus", Order.OrderStatus.INIT);

        order.cancel();

        assertThat(order.getOrderStatus()).isEqualTo(Order.OrderStatus.CANCELED);
    }
}
