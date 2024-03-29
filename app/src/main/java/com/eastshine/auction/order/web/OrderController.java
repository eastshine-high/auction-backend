package com.eastshine.auction.order.web;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.order.application.CancelOrderService;
import com.eastshine.auction.order.application.OrderService;
import com.eastshine.auction.order.application.PlaceOrderService;
import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.OrderMapper;
import com.eastshine.auction.order.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/user-api/v1/orders")
@RestController
public class OrderController {
    private final OrderService orderService;
    private final PlaceOrderService placeOrderService;
    private final CancelOrderService cancelOrderService;

    /**
     * 주문을 진행하고, 등록된 주문의 URI를 응답합니다.
     *
     * @param request 주문 정보.
     * @param authentication 인증 정보.
     * @return 등록된 주문의 URI.
     */
    @PostMapping()
    public ResponseEntity placeOrder(
            @RequestBody @Validated OrderDto.PlaceOrderRequest request,
            Authentication authentication
    ) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        request.setUserInfo(userInfo);
        Order order = placeOrderService.placeOrder(request);
        return ResponseEntity.created(URI.create("/user-api/v1/orders/" + order.getId())).build();
    }

    /**
     * 식별자에 해당하는 주문 정보를 응답합니다.
     *
     * @param orderId 주문 식별자
     * @param authentication 인증 정보.
     * @return 주문 정보.
     */
    @GetMapping("/{orderId}")
    public OrderDto.Info getOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        Order order = orderService.getUserOrderInfo(userInfo.getId(), orderId);
        return OrderMapper.INSTANCE.of(order, order.getOrderItems());
    }

    /**
     * 식별자에 해당하는 주문을 취소합니다.
     *
     * @param id 주문 식별자
     * @param authentication 인증 정보.
     */
    @DeleteMapping("/{id}")
    public void cancelOrder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        cancelOrderService.cancelOrder(userInfo, id);
    }
}
