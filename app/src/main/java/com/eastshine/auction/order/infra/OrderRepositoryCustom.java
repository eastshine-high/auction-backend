package com.eastshine.auction.order.infra;

import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.item.OrderItem;
import com.eastshine.auction.order.domain.item.OrderItemOption;
import com.eastshine.auction.order.web.dto.OrderDto;

import java.util.Optional;

public interface OrderRepositoryCustom {
    Optional<OrderItem> findInitialOrderItemToRegister(OrderDto.PlaceOrderItem request);

    Optional<OrderItemOption> findInitialOrderItemOptionToRegister(OrderDto.PlaceOrderItemOption request);

    Optional<Order> findByIdWithFetchJoin(Long orderId);
}
