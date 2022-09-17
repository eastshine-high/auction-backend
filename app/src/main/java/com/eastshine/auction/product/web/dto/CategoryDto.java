package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.product.domain.category.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryDto {

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplayMain {

        private Integer id;
        private Integer ordering;
        private String name;
        private List<DisplaySub> children = new ArrayList<>();

        public DisplayMain(Category category) {
            id = category.getId();
            ordering = category.getOrdering();
            name = category.getName();
            children = category.getChildren().stream().map(DisplaySub::new).collect(Collectors.toList());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplaySub {
        private Integer id;

        private Integer ordering;

        private String name;

        public DisplaySub(Category category) {
            id = category.getId();
            ordering = category.getOrdering();
            name = category.getName();
        }
    }
}
