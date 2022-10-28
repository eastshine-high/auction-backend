package com.eastshine.auction.order.web;

import com.eastshine.auction.common.model.UserInfo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/user-api/orders")
public class OrderController {
    private final PlaceOrderService placeOrderService;
    private final OrderService orderService;

    /**
     * 주문을 진행하고, 등록된 주문의 URI를 응답합니다.
     *
     * @param request 주문 정보.
     * @param authentication 인증 정보.
     * @return 등록된 주문의 URI.
     */
    @PostMapping()
    public ResponseEntity placeOrder(
            @RequestBody @Validated OrderDto.Request request,
            Authentication authentication
    ) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        request.setOrderer(userInfo.getId());
        Order order = placeOrderService.placeOrder(request);
        return ResponseEntity.created(URI.create("/user-api/orders/" + order.getId())).build();
    }
}