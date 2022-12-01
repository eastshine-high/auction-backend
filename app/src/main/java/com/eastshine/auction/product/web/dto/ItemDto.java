package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.product.domain.item.fragment.DeliveryChargePolicyType;
import com.eastshine.auction.user.domain.seller.SellerLevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class ItemDto {

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
    @NoArgsConstructor
    public static class SearchResponse {
        private long id;
        private String name;
        private int price;
        private String nickname;
        private SellerLevelType sellerLevel;
        private String deliveryChargePolicy;
        private String deliveryChargePolicyDescription;
        private Integer deliveryCharge;

        public SearchResponse(
                long id,
                String name,
                int price,
                DeliveryChargePolicyType deliveryChargePolicy,
                Integer deliveryCharge,
                String nickname,
                SellerLevelType sellerLevel
        ) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.deliveryChargePolicy = deliveryChargePolicy.name();
            this.deliveryChargePolicyDescription = deliveryChargePolicy.getDescription();
            this.deliveryCharge = deliveryCharge;
            this.nickname = nickname;
            this.sellerLevel = sellerLevel;
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemInfo {
        private Long id;
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private String itemOptionsTitle;
        private String nickname;
        private SellerLevelType sellerLevel;
        private ShippingInfo shippingInfo;
        private List<ItemOptionInfo> itemOptions;
    }

    @Getter
    @Builder
    public static class ShippingInfo{
        private String deliveryChargePolicy;
        private String deliveryChargePolicyDescription;
        private Integer deliveryTime;
        private Integer deliveryCharge;
        private Integer freeShipOverAmount;
        private String deliveryMethod;
        private String deliveryMethodDescription;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemOptionInfo {
        private long id;
        private String itemOptionName;
        private Integer additionalPrice;
        private Integer stockQuantity;
        private Integer ordering;
    }
}
