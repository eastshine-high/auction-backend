package com.eastshine.auction.order.repository;

import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.item.OrderItem;
import com.eastshine.auction.order.domain.item.OrderItemOption;
import com.eastshine.auction.order.web.dto.OrderDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.eastshine.auction.order.domain.QOrder.order;
import static com.eastshine.auction.order.domain.item.QOrderItem.orderItem;
import static com.eastshine.auction.product.domain.item.QItem.item;
import static com.eastshine.auction.product.domain.item.option.QItemOption.itemOption;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory query;

    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<OrderItem> findInitialOrderItemToRegister(OrderDto.PlaceOrderItem request) {
        return Optional.ofNullable(
                query.select(Projections.fields(OrderItem.class,
                                item.createdBy.as("sellerId"),
                                item.id.as("itemId"),
                                item.name.as("itemName"),
                                item.price.as("itemPrice"),
                                Expressions.as(Expressions.constant(request.getOrderCount()), "orderCount"),
                                Expressions.as(Expressions.constant(OrderItem.DeliveryStatus.BEFORE_DELIVERY), "deliveryStatus")
                        ))
                        .from(item)
                        .where(item.id.eq(request.getItemId()))
                        .fetchOne()
        );
    }

    @Override
    public Optional<OrderItemOption> findInitialOrderItemOptionToRegister(OrderDto.PlaceOrderItemOption request) {
        return Optional.ofNullable(
                query.select(Projections.fields(OrderItemOption.class,
                                itemOption.id.as("itemOptionId"),
                                itemOption.itemOptionName,
                                itemOption.additionalPrice.coalesce(0).as("additionalPrice"),
                                Expressions.as(Expressions.constant(request.getOrderCount()), "orderCount")
                        ))
                        .from(itemOption)
                        .where(itemOption.id.eq(request.getItemOptionId()))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Order> findByIdWithFetchJoin(Long orderId) {
        return Optional.ofNullable(
                query.selectFrom(order)
                        .join(order.orderItems, orderItem).fetchJoin()
                        .where(order.id.eq(orderId))
                        .fetchOne()
        );
    }
}
