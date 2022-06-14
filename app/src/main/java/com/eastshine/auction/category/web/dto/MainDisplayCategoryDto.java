package com.eastshine.auction.category.web.dto;

import com.eastshine.auction.category.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainDisplayCategoryDto {

    private Integer id;
    private Integer ordering;
    private String name;
    private List<SubDisplayCategoryDto> children = new ArrayList<>();

    public MainDisplayCategoryDto(Category category) {
        id = category.getId();
        ordering = category.getOrdering();
        name = category.getName();
        children = category.getChildren().stream().map(SubDisplayCategoryDto::new).collect(Collectors.toList());
    }
}
