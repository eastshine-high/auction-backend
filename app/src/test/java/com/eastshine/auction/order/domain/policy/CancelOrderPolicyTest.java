package com.eastshine.auction.order.domain.policy;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.IllegalStatusException;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.order.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelOrderPolicyTest extends IntegrationTest {
    private static final Long ORDER_USER_ID = 3L;
    private static final UserInfo ORDER_USER_INFO = UserInfo.builder().id(ORDER_USER_ID).build();

    @Autowired
    CancelOrderPolicy cancelOrderPolicy;

    @Nested
    @DisplayName("validateCancelableOrder 메소드는")
    class Describe_validateCancelableOrder {

        @Test
        @DisplayName("다른 사용자가 주문을 취소할 경우, UnauthorizedException 예외를 던진다.")
        void ContextWithOtherUser() {
            Order order = Order.builder().userId(ORDER_USER_ID).build();
            UserInfo otherUser = UserInfo.builder().id(999999999L).build();

            assertThatThrownBy(() -> cancelOrderPolicy.validateCancelableOrder(order, otherUser))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorCode.ORDER_INACCESSIBLE.getErrorMsg());
        }

        @DisplayName("주문 취소가 가능한 상태일 경우, 예외를 던지지 않는다.")
        @ParameterizedTest
        @EnumSource(names = {"INIT", "ORDER_COMPLETE"})
        void contextWithCancelableOrderState(Order.OrderStatus orderStatus) {
            Order order = Order.builder().userId(ORDER_USER_ID).build();
            ReflectionTestUtils.setField(order, "orderStatus", orderStatus);

            cancelOrderPolicy.validateCancelableOrder(order, ORDER_USER_INFO);
        }

        @DisplayName("주문 취소가 불가한 상태일 경우, IllegalStatusException을 던진다.")
        @ParameterizedTest
        @EnumSource(names = {"IN_DELIVERY", "DELIVERY_PREPARE", "DELIVERY_COMPLETE", "CANCELED"})
        void contextWithNonCancelableOrderState(Order.OrderStatus orderStatus) {
            Order order = Order.builder().userId(ORDER_USER_ID).build();
            ReflectionTestUtils.setField(order, "orderStatus", orderStatus);

            assertThatThrownBy(() -> cancelOrderPolicy.validateCancelableOrder(order, ORDER_USER_INFO))
                    .isInstanceOf(IllegalStatusException.class)
                    .hasMessage(ErrorCode.NON_CANCELABLE_ORDER_STATE.getErrorMsg());
        }
    }
}
