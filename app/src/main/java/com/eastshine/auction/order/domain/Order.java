package com.eastshine.auction.order.domain;


import com.eastshine.auction.common.model.BaseEntity;
import com.eastshine.auction.order.domain.fragment.DeliveryFragment;
import com.eastshine.auction.order.domain.item.OrderLine;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false, of = "id")
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    private Long userId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderLine> orderLines = Lists.newArrayList();

    @Embedded
    private DeliveryFragment deliveryFragment;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Getter
    @RequiredArgsConstructor
    public enum OrderStatus {
        INIT("주문시작"),
        ORDER_COMPLETE("주문완료"),
        DELIVERY_PREPARE("배송준비"),
        IN_DELIVERY("배송중"),
        DELIVERY_COMPLETE("배송완료");

        private final String description;
    }

    @Builder
    public Order(
            Long userId,
            DeliveryFragment deliveryFragment
    ) {
        this.userId = userId;
        this.deliveryFragment = deliveryFragment;
        this.orderStatus = OrderStatus.INIT;
    }

    public void setOrderLines(List<OrderLine> orderLineList) {
        this.orderLines = orderLineList;
    }

    public long getTotalAmount() {
        return orderLines.stream()
                .mapToLong(OrderLine::calculateTotalAmount)
                .sum();
    }
}
