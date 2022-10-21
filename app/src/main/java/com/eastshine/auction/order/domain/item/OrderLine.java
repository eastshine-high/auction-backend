package com.eastshine.auction.order.domain.item;

import com.eastshine.auction.order.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "order_line")
@Entity
public class OrderLine {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "order_line_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private Long sellerId;
    private Long itemId;
    private String itemName;
    private Integer itemPrice;
    private Long itemOptionId;
    private String itemOptionName;
    private Integer itemOptionPrice;
    private Integer orderCount;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Getter
    @AllArgsConstructor
    public enum DeliveryStatus {
        BEFORE_DELIVERY("배송전"),
        DELIVERY_PREPARE("배송준비중"),
        DELIVERING("배송중"),
        COMPLETE_DELIVERY("배송완료");

        private final String description;
    }

    @Builder
    public OrderLine(
            Order order,
            Long sellerId,
            Long itemId,
            String itemName,
            Integer itemPrice,
            Long itemOptionId,
            String itemOptionName,
            Integer itemOptionPrice,
            Integer orderCount
    ) {
        this.order = order;
        this.sellerId = sellerId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemOptionId = itemOptionId;
        this.itemOptionName = itemOptionName;
        this.itemOptionPrice = itemOptionPrice;
        this.orderCount = orderCount;
        this.deliveryStatus = DeliveryStatus.BEFORE_DELIVERY;
    }

    public long calculateTotalAmount() {
        return (itemPrice + itemOptionPrice) * orderCount;
    }
}
