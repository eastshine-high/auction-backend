package com.eastshine.auction.order.domain.item;

import com.eastshine.auction.common.model.BaseTimeEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Getter
@EqualsAndHashCode(callSuper=false, of = "id")
@NoArgsConstructor
@Entity
public class OrderItemOption extends BaseTimeEntity {

    @Id @Column(name = "order_item_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "order_item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderItem orderItem;
    private Long itemOptionId;
    private String itemOptionName;
    private Integer additionalPrice;
    private Integer orderCount;

    @Builder
    public OrderItemOption(Long itemOptionId, String itemOptionName, Integer additionalPrice, Integer orderCount) {
        this.itemOptionId = itemOptionId;
        this.itemOptionName = itemOptionName;
        this.additionalPrice = additionalPrice;
        this.orderCount = orderCount;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public long calculateTotalAmount() {
        return (orderItem.getItemPrice() + additionalPrice) * orderCount;
    }
}
