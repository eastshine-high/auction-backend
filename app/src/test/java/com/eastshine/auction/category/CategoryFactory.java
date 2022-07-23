package com.eastshine.auction.category;

import com.eastshine.auction.category.application.CategoryService;
import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.category.domain.CategoryRepository;
import com.eastshine.auction.category.web.dto.CategoryRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryFactory {

    @Autowired CategoryService categoryService;
    @Autowired CategoryRepository categoryRepository;

    public Category createCategory(Integer id, String name, Integer ordering) {
        return createCategory(id, null, name, ordering);
    }

    public Category createCategory(Integer id, Integer parentId, String name, Integer ordering) {
        CategoryRegistrationRequest registrationDto = CategoryRegistrationRequest.builder()
                .id(id)
                .parentId(parentId)
                .name(name)
                .ordering(ordering)
                .build();
        return categoryService.registerCategory(registrationDto);
    }

    public void deleteAllCategory() {
        categoryRepository.deleteAll();
    }
}
