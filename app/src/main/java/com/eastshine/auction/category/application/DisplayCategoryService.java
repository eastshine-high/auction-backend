package com.eastshine.auction.category.application;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.category.domain.CategoryRepository;
import com.eastshine.auction.category.web.dto.MainDisplayCategoryDto;
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
