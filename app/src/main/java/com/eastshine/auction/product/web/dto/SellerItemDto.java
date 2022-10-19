package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.product.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SellerItemDto {

    @ToString
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationRequest {

        @NotBlank
        private String name;

        @NotNull
        private Integer categoryId;

        @NotNull
        @Range(min = 1000)
        private Integer price;

        @NotNull
        private Boolean onSale;
        private Integer stockQuantity;
        private String itemOptionsTitle;
        private List<ItemOption> itemOptions;

        public Item toEntity() {
            return Item.builder()
                    .name(name)
                    .categoryId(categoryId)
                    .price(price)
                    .onSale(onSale)
                    .stockQuantity(stockQuantity)
                    .itemOptionsTitle(itemOptionsTitle)
                    .itemOptions(new ArrayList<>())
                    .build();
        }

        @ToString
        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ItemOption {

            @NotBlank
            private String itemOptionName;

            @NotNull
            private Integer ordering;
            private Integer stockQuantity;
            private Integer additionalPrice;

            public com.eastshine.auction.product.domain.item.option.ItemOption toEntity(Item item) {
                return com.eastshine.auction.product.domain.item.option.ItemOption.builder()
                        .item(item)
                        .itemOptionName(itemOptionName)
                        .additionalPrice(additionalPrice)
                        .stockQuantity(stockQuantity)
                        .ordering(ordering)
                        .build();
            }
        }
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class Info{
        private Long id;
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private Integer categoryId;
        private Boolean onSale;
        private String itemOptionsTitle;
        private List<ItemOption> itemOptions;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class ItemOption {
            private Long id;
            private String itemOptionName;
            private Integer additionalPrice;
            private Integer stockQuantity;
            private Integer ordering;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchRequest {
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private Integer categoryId;
        private Boolean onSale;
        private String itemOptionsTitle;
        private List<ItemOption> itemOptions;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ItemOption {
            private Long id;
            private String itemOptionName;
            private Integer additionalPrice;
            private Integer stockQuantity;
            private Integer ordering;
        }
    }
}
