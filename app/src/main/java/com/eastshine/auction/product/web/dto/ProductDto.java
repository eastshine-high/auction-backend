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
        private String productOptionsTitle;
        private String nickname;
        private SellerLevelType sellerLevel;
        private List<ProductOption> productOptions;

        public void setProductOptions(List<ProductOption> productOptions) {
            this.productOptions = productOptions;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class ProductOption {
            private long id;
            private String productOptionName;
            private Integer stockQuantity;
            private Integer ordering;

            public ProductOption(com.eastshine.auction.product.domain.product.option.ProductOption productOption) {
                this.id = productOption.getId();
                this.productOptionName = productOption.getProductOptionName();
                this.stockQuantity = productOption.getStockQuantity();
                this.ordering = productOption.getOrdering();
            }
        }
    }
}
