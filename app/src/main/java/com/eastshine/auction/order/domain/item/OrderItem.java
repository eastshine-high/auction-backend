package com.eastshine.auction.order.domain.item;

import com.eastshine.auction.common.model.BaseTimeEntity;
import com.eastshine.auction.order.domain.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper=false, of = "id")
@NoArgsConstructor
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @JsonIgnore // Producer의 ObjectMapper에서 발생하는 recursion 오류 방지를 위해 사용
    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.PERSIST)
    private List<OrderItemOption> orderItemOptions = new ArrayList<>();

    private Long sellerId;
    private Long itemId;
    private String itemName;
    private Integer itemPrice;
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
    public OrderItem(
            Long sellerId,
            Long itemId,
            String itemName,
            Integer itemPrice,
            Integer orderCount
    ) {
        this.sellerId = sellerId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.orderCount = orderCount;
        this.deliveryStatus = DeliveryStatus.BEFORE_DELIVERY;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void addOrderItemOption(OrderItemOption orderItemOption) {
        orderItemOption.setOrderItem(this);
        orderItemOptions.add(orderItemOption);
    }

    public long calculateTotalAmount() {
        if(CollectionUtils.isEmpty(orderItemOptions)){
            return itemPrice * orderCount;
        }

        return orderItemOptions.stream()
                .mapToLong(OrderItemOption::calculateTotalAmount)
                .sum();
    }
}
