package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.option.ProductOption;
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
import java.util.stream.Collectors;

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
        private List<ProductOptionDto> productOptionDtos;

        public Product toProductEntity() {
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

        public List<ProductOption> toProductOptionEntities(Product product) {
            return productOptionDtos.stream()
                    .map(productOptionDto -> productOptionDto.toEntity(product))
                    .collect(Collectors.toList());
        }

        @ToString
        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProductOptionDto {

            @NotBlank
            private String productOptionName;

            @NotNull
            private Integer ordering;
            private Integer stockQuantity;

            public ProductOption toEntity(Product product) {
                return com.eastshine.auction.product.domain.product.option.ProductOption.builder()
                        .product(product)
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
        private String productOptionsTitle;
        private List<ProductOptionDto> productOptions;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProductOptionDto {
            private Long id;
            private String productOptionName;
            private Integer stockQuantity;
            private Integer ordering;
        }
    }
}
