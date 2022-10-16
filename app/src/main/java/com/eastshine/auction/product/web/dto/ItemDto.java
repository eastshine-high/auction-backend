package com.eastshine.auction.product.web.dto;

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

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info {
        private Long id;
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private String itemOptionsTitle;
        private String nickname;
        private SellerLevelType sellerLevel;
        private List<ItemOption> itemOptions;

        public void setItemOptions(List<ItemOption> itemOptions) {
            this.itemOptions = itemOptions;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class ItemOption {
            private long id;
            private String itemOptionName;
            private Integer stockQuantity;
            private Integer ordering;

            public ItemOption(com.eastshine.auction.product.domain.item.option.ItemOption itemOption) {
                this.id = itemOption.getId();
                this.itemOptionName = itemOption.getItemOptionName();
                this.stockQuantity = itemOption.getStockQuantity();
                this.ordering = itemOption.getOrdering();
            }
        }
    }
}