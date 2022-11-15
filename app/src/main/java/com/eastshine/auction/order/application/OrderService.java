package com.eastshine.auction.order.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.item.OrderItem;
import com.eastshine.auction.order.domain.item.OrderItemOption;
import com.eastshine.auction.order.infra.OrderRepository;
import com.eastshine.auction.order.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public Order registerOrder(OrderDto.PlaceOrderRequest request) {
        Order order = request.toEntity();
        request.getOrderItems().forEach(orderItem ->
                        order.addOrderItem(getOrderItemToRegister(orderItem)));
        return orderRepository.save(order);
    }

    private OrderItem getOrderItemToRegister(OrderDto.PlaceOrderItem request) {
        OrderItem orderItem = orderRepository.findInitialOrderItemToRegister(request)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_ITEM_NOT_FOUND));

        if(!CollectionUtils.isEmpty(request.getOrderItemOptions())){
            request.getOrderItemOptions().forEach(orderItemOption ->
                    orderItem.addOrderItemOption(getOrderItemOptionToRegister(orderItemOption)));
        }

        return orderItem;
    }

    private OrderItemOption getOrderItemOptionToRegister(OrderDto.PlaceOrderItemOption request) {
        return orderRepository.findInitialOrderItemOptionToRegister(request)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_ITEM_OPTION_NOT_FOUND));
    /* 교훈 : 프론트엔드(More flexible)에서 자료구조를 잘 정의해서 내려와야 한다.
       그렇지 않으면 Grouping등을 이용해 자료를 가공할 수는 있지만, 백엔드 코드가 지저분해지고 가독성이 떨어진다. */
    }

    @Transactional(readOnly = true)
    public Order getUserOrderInfo(Long userId, Long orderId) {
        return orderRepository.findByIdWithFetchJoin(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
    }
}
