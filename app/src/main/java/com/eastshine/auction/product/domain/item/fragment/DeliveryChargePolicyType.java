package com.eastshine.auction.product.domain.item.fragment;

public enum DeliveryChargePolicyType {
    FREE("무료배송"),
    CHARGE("유료배송"),
    CHARGE_RECEIVED("착불배송"),
    CONDITIONAL("조건부 무료배송");

    DeliveryChargePolicyType(String description) {
        this.description = description;
    }

    private final String description;

    public String getDescription() {
        return description;
    }
}
