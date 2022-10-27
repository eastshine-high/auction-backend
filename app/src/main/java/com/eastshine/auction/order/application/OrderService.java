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

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public Order registerOrder(OrderDto.Request request) {
        Order order = request.toEntity();
        addOrderLinesToOrder(request.getOrderLines(), order);
        return orderRepository.save(order);
    }

    private void addOrderLinesToOrder(List<OrderDto.OrderLineRequest> requests, Order target) {
        requests.stream()
                .forEach(orderLineRequest -> {
                        OrderLine orderLine = orderRepository.findOrderLineForInitialRegistration(orderLineRequest)
                                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_ITEM_NOT_FOUND));
                        target.addOrderLine(orderLine);
                });
    }
}
