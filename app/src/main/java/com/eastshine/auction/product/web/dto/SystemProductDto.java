package com.eastshine.auction.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class SystemProductDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecreaseStock{
        List<Product> products;
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Product{
            private Long id;
            private Integer quantity;
            private List<ProductOption> productOptions;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProductOption{
            private Long id;
            private Integer quantity;
        }
    }
}
