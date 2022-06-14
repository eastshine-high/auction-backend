package com.eastshine.auction.category.web.dto;

import com.eastshine.auction.category.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubDisplayCategoryDto {
    private Integer id;

    private Integer ordering;

    private String name;

    public SubDisplayCategoryDto(Category category) {
        id = category.getId();
        ordering = category.getOrdering();
        name = category.getName();
    }
}
