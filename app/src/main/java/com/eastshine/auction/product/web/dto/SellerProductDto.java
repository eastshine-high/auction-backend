package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.product.domain.product.Product;
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

public class SellerProductDto {

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
        private String productOptionsTitle;
        private List<ProductOption> productOptions;

        public Product toEntity() {
            return Product.builder()
                    .name(name)
                    .categoryId(categoryId)
                    .price(price)
                    .onSale(onSale)
                    .stockQuantity(stockQuantity)
                    .productOptionsTitle(productOptionsTitle)
                    .productOptions(new ArrayList<>())
                    .build();
        }

        @ToString
        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProductOption {

            @NotBlank
            private String productOptionName;

            @NotNull
            private Integer ordering;
            private Integer stockQuantity;

            public com.eastshine.auction.product.domain.product.option.ProductOption toEntity() {
                return com.eastshine.auction.product.domain.product.option.ProductOption.builder()
                        .productOptionName(productOptionName)
                        .stockQuantity(stockQuantity)
                        .ordering(ordering)
                        .build();
            }
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
        private List<ProductOption> productOptions;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProductOption {
            private Long id;
            private String productOptionName;
            private Integer stockQuantity;
            private Integer ordering;
        }
    }
}
