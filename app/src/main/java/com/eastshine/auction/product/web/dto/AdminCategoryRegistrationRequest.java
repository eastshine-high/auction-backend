package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.product.domain.category.Category;
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
public class AdminCategoryRegistrationRequest {

    @NotNull
    private Integer id;
    private Integer parentId;

    @NotBlank
    private String name;

    @NotNull
    private Integer ordering;

    public Category toEntity(Category parentCategory) {
        return Category.builder()
                .id(id)
                .parent(parentCategory)
                .name(name)
                .ordering(ordering)
                .build();
    }
}
