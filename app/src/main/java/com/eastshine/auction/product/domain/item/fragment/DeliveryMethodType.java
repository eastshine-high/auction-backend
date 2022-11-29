package com.eastshine.auction.product.domain.item.fragment;

public enum DeliveryMethodType {
    SEQUENCIAL("일반배송(순차배송)"),
    COLD_FRESH("신선냉동"),
    MAKE_ORDER("주문제작"),
    AGENT_BUY("구매대행"),
    VENDOR_DIRECT("설치배송 또는 판매자 직접 전달");

    DeliveryMethodType(String description) {
        this.description = description;
    }

    private final String description;

    public String getDescription() {
        return description;
    }
}
