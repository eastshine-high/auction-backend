package com.eastshine.auction.order.infra;

import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.item.OrderLine;
import com.eastshine.auction.order.web.dto.OrderDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.eastshine.auction.order.domain.QOrder.order;
import static com.eastshine.auction.order.domain.item.QOrderLine.orderLine;
import static com.eastshine.auction.product.domain.item.QItem.item;
import static com.eastshine.auction.product.domain.item.option.QItemOption.itemOption;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory query;

    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<OrderLine> findOrderLineForInitialRegistration(OrderDto.OrderLineRequest request) {
        return Optional.ofNullable(
                query.select(Projections.fields(OrderLine.class,
                                item.createdBy.as("sellerId"),
                                item.id.as("itemId"),
                                item.name.as("itemName"),
                                item.price.as("itemPrice"),
                                itemOption.id.as("itemOptionId"),
                                itemOption.itemOptionName,
                                itemOption.additionalPrice.coalesce(0).as("itemOptionPrice"),
                                Expressions.as(Expressions.constant(request.getOrderCount()), "orderCount"),
                                Expressions.as(Expressions.constant(OrderLine.DeliveryStatus.BEFORE_DELIVERY), "deliveryStatus")
                        ))
                        .from(item)
                        .leftJoin(item.itemOptions, itemOption).on(itemOption.id.eq(request.getItemOptionId()))
                        .where(item.id.eq(request.getItemId()))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Order> findUserOrderInfo(Long orderId, Long userId) {
        return Optional.ofNullable(
                query.selectFrom(order)
                        .join(order.orderLines, orderLine).fetchJoin()
                        .where(order.id.eq(orderId)
                                .and(order.userId.eq(userId)))
                        .fetchOne()
        );
    }
}
