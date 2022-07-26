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
public class ProductRegistrationRequest {

    @NotBlank
    private String name;

    @NotNull
    private Integer categoryId;

    @NotNull
    @Range(min = 1000)
    private Integer price;

    @NotNull
    private Integer stockQuantity;

    @NotNull
    private Boolean onSale;
}
