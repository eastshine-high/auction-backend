package com.eastshine.auction.product.application;

import com.eastshine.auction.order.domain.item.OrderItem;
import com.eastshine.auction.order.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Component
public class ProductStockService {
    private final ItemStockService itemStockService;
    private final ItemOptionStockService itemOptionStockService;

    public void decreaseStock(OrderDto.PlaceOrderItem orderItem) {
        if(CollectionUtils.isEmpty(orderItem.getOrderItemOptions())){
            itemStockService.decreaseStockWithLock(orderItem.getItemId(), orderItem.getOrderCount());
        } else {
            orderItem.getOrderItemOptions().stream().forEach(orderItemOption ->
                    itemOptionStockService.decreaseStockWithLock(orderItemOption.getItemOptionId(), orderItemOption.getOrderCount()));
        }
    }

    public void increaseStock(OrderItem orderItem) {
        if(CollectionUtils.isEmpty(orderItem.getOrderItemOptions())){
            itemStockService.increaseStockWithLock(orderItem.getItemId(), orderItem.getOrderCount());
        } else {
            orderItem.getOrderItemOptions().stream().forEach(orderItemOption ->
                    itemOptionStockService.increaseStockWithLock(orderItemOption.getItemOptionId(), orderItemOption.getOrderCount()));
        }
    }
}
