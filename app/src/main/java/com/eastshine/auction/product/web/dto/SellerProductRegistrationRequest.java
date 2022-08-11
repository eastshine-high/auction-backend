package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.product.domain.Product;
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

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProductRegistrationRequest {

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

        private Integer stockQuantity;

        @NotNull
        private Integer ordering;

        public com.eastshine.auction.product.domain.option.ProductOption toEntity() {
            return com.eastshine.auction.product.domain.option.ProductOption.builder()
                    .productOptionName(productOptionName)
                    .stockQuantity(stockQuantity)
                    .ordering(ordering)
                    .build();
        }
    }
}
