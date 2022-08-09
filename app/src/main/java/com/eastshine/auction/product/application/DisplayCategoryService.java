package com.eastshine.auction.product.application;

import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.domain.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.MainDisplayCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DisplayCategoryService {
    private final CategoryRepository categoryRepository;

    @Cacheable(value = "displayCategories", cacheManager = "cacheManager")
    public List<MainDisplayCategoryDto> getDisplayCategories() {
        List<Category> categories = categoryRepository.findDisplayCategories();
        return categories.stream()
                .map(MainDisplayCategoryDto::new)
                .collect(Collectors.toList());
    }
}
