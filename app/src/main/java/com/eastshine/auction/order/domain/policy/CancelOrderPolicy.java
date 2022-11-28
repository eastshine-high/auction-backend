package com.eastshine.auction.order.domain.policy;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.IllegalStatusException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.order.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class CancelOrderPolicy {

    public void validateCancelableOrder(Order order, UserInfo userInfo) {
        order.validateAccessibleUser(userInfo.getId());
        validateCancelableState(order);
    }

    private void validateCancelableState(Order order) {
        if (order.getOrderStatus() == Order.OrderStatus.IN_DELIVERY
            || order.getOrderStatus() == Order.OrderStatus.DELIVERY_PREPARE
            || order.getOrderStatus() == Order.OrderStatus.DELIVERY_COMPLETE
            || order.getOrderStatus() == Order.OrderStatus.CANCELED) {

            throw new IllegalStatusException(ErrorCode.NON_CANCELABLE_ORDER_STATE);
        }
    }
}
