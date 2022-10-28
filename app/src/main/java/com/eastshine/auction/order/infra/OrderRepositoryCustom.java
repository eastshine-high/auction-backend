package com.eastshine.auction.order.infra;

import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.item.OrderLine;
import com.eastshine.auction.order.web.dto.OrderDto;

import java.util.Optional;

public interface OrderRepositoryCustom {
    Optional<OrderLine> findOrderLineForInitialRegistration(OrderDto.OrderLineRequest request);

    Optional<Order> findUserOrderInfo(Long orderId, Long userId);
}
