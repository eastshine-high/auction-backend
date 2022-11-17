package com.eastshine.auction.order.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.order.adaptor.CancelOrderProducer;
import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.infra.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CancelOrderService {
    private final OrderRepository orderRepository;
    private final CancelOrderProducer cancelOrderProducer;

    @Transactional
    public void cancelOrder(UserInfo userInfo, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
        order.validateAccessibleUser(userInfo.getId());
        order.cancel(); // 1. 주문 취소
        cancelOrderProducer.increaseStock(order); // 2. 재고 증가
        cancelOrderProducer.sendMail(userInfo, order); // 3. 메일 발송 이벤트 발행
    }
}
