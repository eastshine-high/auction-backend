package com.eastshine.auction.product.web;

import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.domain.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.MainDisplayCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping("/api/display/categories")
    @Cacheable(value = "displayCategories", cacheManager = "cacheManager")
    public List<MainDisplayCategoryDto> getDisplayCategories() {
        List<Category> categories = categoryRepository.findDisplayCategories();
        return categories.stream()
                .map(MainDisplayCategoryDto::new)
                .collect(Collectors.toList());
    }
}
