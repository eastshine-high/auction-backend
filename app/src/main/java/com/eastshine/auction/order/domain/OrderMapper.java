package com.eastshine.auction.order.domain;

import com.eastshine.auction.order.domain.item.OrderLine;
import com.eastshine.auction.order.web.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mappings({
            @Mapping(source = "order.id", target = "orderId"),
            @Mapping(source = "order.deliveryFragment", target = "deliveryInfo"),
            @Mapping(expression = "java(order.getTotalAmount())", target = "totalAmount"),
            @Mapping(expression = "java(order.getOrderStatus().name())", target = "orderStatus"),
            @Mapping(expression = "java(order.getOrderStatus().getDescription())", target = "orderStatusDescription")
    })
    OrderDto.Info of(Order order, List<OrderLine> orderItemList);

    @Mappings({
            @Mapping(expression = "java(orderLine.getDeliveryStatus().name())", target = "deliveryStatus"),
            @Mapping(expression = "java(orderLine.getDeliveryStatus().getDescription())", target = "deliveryStatusDescription"),
            @Mapping(expression = "java(orderLine.calculateTotalAmount())", target = "totalAmount")
    })
    OrderDto.OrderLineInfo of(OrderLine orderLine);
}
