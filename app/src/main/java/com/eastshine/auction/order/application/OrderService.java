package com.eastshine.auction.order.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.item.OrderLine;
import com.eastshine.auction.order.infra.OrderRepository;
import com.eastshine.auction.order.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public Order registerOrder(OrderDto.Request request) {
        Order order = request.toEntity();
        request.getOrderLines().stream().forEach(orderLineRequest ->
                order.addOrderLine(getOrderLineToRegister(orderLineRequest)));
        return orderRepository.save(order);
    }

    private OrderLine getOrderLineToRegister(OrderDto.OrderLineRequest request) {
        return orderRepository.findOrderLineForInitialRegistration(request)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_ITEM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Order getUserOrderInfo(Long orderId, Long userId) {
        return orderRepository.findUserOrderInfo(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
    }
}
