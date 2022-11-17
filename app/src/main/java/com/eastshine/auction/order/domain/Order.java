package com.eastshine.auction.order.domain;


import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.common.model.BaseTimeEntity;
import com.eastshine.auction.order.domain.fragment.DeliveryFragment;
import com.eastshine.auction.order.domain.item.OrderItem;
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper=false, of = "id")
@NoArgsConstructor
@Table(name = "orders")
@Entity
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private DeliveryFragment deliveryFragment;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Getter
    @RequiredArgsConstructor
    public enum OrderStatus {
        INIT("주문시작"),
        ORDER_COMPLETE("주문완료"),
        CANCELED("주문취소"),
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

    public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public void validateAccessibleUser(Long accessorId) {
        if(this.userId != accessorId){
            throw new UnauthorizedException(ErrorCode.ORDER_INACCESSIBLE);
        }
    }

    public void cancel() {
        orderStatus = OrderStatus.CANCELED;
    }

    public long calculateTotalAmount() {
        return orderItems.stream()
                .mapToLong(OrderItem::calculateTotalAmount)
                .sum();
    }
}
