package com.eastshine.auction.product.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerItemPatchValidationBean {

    @NotBlank
    private String name;

    @Range(min = 1000)
    private Integer price;

    private Integer stockQuantity;

    @NotNull
    private Boolean onSale;

    @NotNull
    private Integer categoryId;

    private List<PatchProductOption> productOptions;

    @Getter
    @Setter
    public static class PatchProductOption {

        @NotBlank
        private String productOptionName;

        private Integer stockQuantity;

        @NotNull
        private Integer ordering;
    }
}
