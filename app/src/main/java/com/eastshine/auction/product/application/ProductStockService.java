package com.eastshine.auction.product.application;

import com.eastshine.auction.order.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class ProductStockService {
    private final ItemStockService itemStockService;
    private final ItemOptionStockService itemOptionStockService;

    public void decreaseStock(OrderDto.OrderLineRequest orderLine) {
        if(Objects.isNull(orderLine.getItemOptionId()) || orderLine.getItemOptionId() == 0L){
            itemStockService.decreaseStockWithLock(orderLine.getItemId(), orderLine.getOrderCount());
        } else {
            itemOptionStockService.decreaseStockWithLock(orderLine.getItemOptionId(), orderLine.getOrderCount());
        }
    }
}
