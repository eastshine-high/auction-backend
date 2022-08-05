package com.eastshine.auction.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProductPatchValidationBean {

    @NotBlank
    private String name;

    @Range(min = 1000)
    private Integer price;

    @NotNull
    private Integer stockQuantity;

    @NotNull
    private Boolean onSale;

    @NotNull
    private PatchCategory category;

    @Getter
    @Setter
    public static class PatchCategory {

        @NotNull
        private Integer id;
    }
}
