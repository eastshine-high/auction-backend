package com.eastshine.auction.product.web;

import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.repository.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.CategoryDto;
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

    @GetMapping("/api/v1/display/categories")
    @Cacheable(value = "displayCategories", cacheManager = "cacheManager")
    public List<CategoryDto.DisplayMain> getDisplayCategories() {
        List<Category> categories = categoryRepository.findDisplayCategories();
        return categories.stream()
                .map(CategoryDto.DisplayMain::new)
                .collect(Collectors.toList());
    }
}
