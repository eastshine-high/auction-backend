package com.eastshine.auction.product.domain.item.fragment;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@NoArgsConstructor
@Embeddable
public class ShippingFragment {
    @Enumerated(value = EnumType.STRING) private DeliveryChargePolicyType deliveryChargePolicy; // 배송 요금 정책
    @Enumerated(value = EnumType.STRING) private DeliveryMethodType deliveryMethod; // 배송 방법
    private Integer deliveryTime; // 배송 기간
    private Integer deliveryCharge; // 배송료
    private Integer freeShipOverAmount; // 조건부 배송 무료 금액
    @Embedded private ReturnFragment returnFragment;

    @Builder
    public ShippingFragment(
            DeliveryChargePolicyType deliveryChargePolicy,
            Integer deliveryTime,
            Integer deliveryCharge,
            Integer freeShipOverAmount,
            DeliveryMethodType deliveryMethod,
            ReturnFragment returnFragment
    ) {
        this.deliveryChargePolicy = deliveryChargePolicy;
        this.deliveryTime = deliveryTime;
        this.deliveryCharge = deliveryCharge;
        this.freeShipOverAmount = freeShipOverAmount;
        this.deliveryMethod = deliveryMethod;
        this.returnFragment = returnFragment;
    }
}
