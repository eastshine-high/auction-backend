package com.eastshine.auction.order.application;

import com.eastshine.auction.order.adaptor.PlaceOrderProducer;
import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.web.dto.OrderDto;
import com.eastshine.auction.product.application.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PlaceOrderService {
    private final ProductStockService productStockService;
    private final OrderService orderService;
    private final PlaceOrderProducer placeOrderProducer;

    @Transactional
    public Order placeOrder(OrderDto.PlaceOrderRequest request) {
        // Todo 1. 결제 검증 : '총 결제 금액'과 '총 물품 가격 합계'를 비교
        request.getOrderItems().stream()
                .forEach(productStockService::decreaseStock); // 2. 재고 차감
        Order registeredOrder = orderService.registerOrder(request);// 3. 주문 등록
        placeOrderProducer.sendMail(request.getUserInfo(), registeredOrder); // 4. 메일 발송 이벤트 발행
        return registeredOrder;
    }
}
