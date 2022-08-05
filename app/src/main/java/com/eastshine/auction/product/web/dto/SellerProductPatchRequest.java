package com.eastshine.auction.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Getter
    @Setter
    public static class PatchCategory {
        private Integer id;
    }
}
