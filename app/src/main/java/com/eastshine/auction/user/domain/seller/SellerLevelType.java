package com.eastshine.auction.user.domain.seller;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SellerLevelType {
    NEW("신규가입"),
    LEVEL_ONE("일반"),
    LEVEL_TWO("판매우수셀러"),
    TOP_RATED("최우수셀러");

    private String title;

    SellerLevelType(String title) {
        this.title = title;
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return title;
    }
}
