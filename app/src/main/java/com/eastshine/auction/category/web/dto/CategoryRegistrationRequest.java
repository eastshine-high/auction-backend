package com.eastshine.auction.category.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRegistrationRequest {

    @NotNull
    private Integer id;
    private Integer parentId;

    @NotNull
    private Integer ordering;

    @NotBlank
    private String name;
}
