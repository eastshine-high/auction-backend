package com.eastshine.auction.order.domain;

import com.eastshine.auction.order.domain.item.OrderItem;
import com.eastshine.auction.order.domain.item.OrderItemOption;
import com.eastshine.auction.order.web.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mappings({
            @Mapping(source = "order.id", target = "orderId"),
            @Mapping(source = "order.deliveryFragment", target = "deliveryInfo"),
            @Mapping(expression = "java(order.calculateTotalAmount())", target = "totalAmount"),
            @Mapping(expression = "java(order.getOrderStatus().name())", target = "orderStatus"),
            @Mapping(expression = "java(order.getOrderStatus().getDescription())", target = "orderStatusDescription")
    })
    OrderDto.Info of(Order order, Set<OrderItem> orderItemSet);

    @Mappings({
            @Mapping(expression = "java(orderItem.getDeliveryStatus().name())", target = "deliveryStatus"),
            @Mapping(expression = "java(orderItem.getDeliveryStatus().getDescription())", target = "deliveryStatusDescription"),
            @Mapping(expression = "java(orderItem.calculateTotalAmount())", target = "totalAmount")
    })
    OrderDto.OrderItemInfo of(OrderItem orderItem);

    @Mappings({
            @Mapping(expression = "java(orderItemOption.calculateTotalAmount())", target = "totalAmount")
    })
    OrderDto.OrderItemOptionInfo of(OrderItemOption orderItemOption);
}
