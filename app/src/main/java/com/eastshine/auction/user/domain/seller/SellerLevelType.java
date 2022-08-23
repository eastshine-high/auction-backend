package com.eastshine.auction.user.domain.seller;

public enum SellerLevelType {
    NEW("신규가입"),
    LEVEL_ONE("일반"),
    LEVEL_TWO("판매우수셀러"),
    TOP_RATED("최우수셀러");

    private String name;

    SellerLevelType(String name) {
        this.name = name;
    }
}
