package com.eastshine.auction.order.application;

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

    @Transactional
    public Order placeOrder(OrderDto.Request request) {
        // order.getTotalAmount(); Todo 1. 결제 검증 : '총 결제 금액'과 '총 물품 가격 합계'를 비교
        request.getOrderLines().stream().forEach(productStockService::decreaseStock); // 2. 재고 차감
        return orderService.registerOrder(request); // 3. 주문 등록

        /** Todo (Pub Sub)
         * 주문 등록 ->
         * 1. 포인트 적립(사용자) - 구매의 1% 적립
         * 2. 메일 발송(시스템)
         */
    }
}
