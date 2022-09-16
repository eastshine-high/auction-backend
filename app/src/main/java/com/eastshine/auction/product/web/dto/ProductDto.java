package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.user.domain.seller.SellerLevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
public class ProductDto {

    @ToString
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchCondition{

        @NotBlank
        String name;
        Integer categoryId;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchResponse {
        private long id;
        private String name;
        private int price;
        private String nickname;
        private SellerLevelType sellerLevel;
    }
}
