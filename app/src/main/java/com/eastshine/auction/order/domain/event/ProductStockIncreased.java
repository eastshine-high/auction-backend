package com.eastshine.auction.order.domain.event;

import com.eastshine.auction.order.domain.item.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockIncreased {
    private List<OrderItem> orderItems;
}
