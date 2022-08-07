package com.eastshine.auction.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProductPatchRequest {
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private Boolean onSale;
    private SellerProductPatchRequest.PatchCategory category;
    private List<ProductOption> productOptions;

    @Getter
    @Setter
    public static class PatchCategory {
        private Integer id;
    }

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
