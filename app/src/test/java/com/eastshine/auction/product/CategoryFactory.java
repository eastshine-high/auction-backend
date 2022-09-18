package com.eastshine.auction.product;

import com.eastshine.auction.product.application.AdminCategoryService;
import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.domain.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryFactory {
    @Autowired AdminCategoryService adminCategoryService;
    @Autowired CategoryRepository categoryRepository;
    private int ordering = 1;

    public Category createCategory(Integer id, String name, Integer ordering) {
        return createCategory(id, null, name, ordering);
    }

    public Category createCategory(Integer id, String name) {
        return createCategory(id, null, name, ordering++);
    }

    public Category createCategory(Integer id, Integer parentId, String name, Integer ordering) {
        AdminCategoryDto.RegistrationRequest registrationDto = AdminCategoryDto.RegistrationRequest.builder()
                .id(id)
                .parentId(parentId)
                .name(name)
                .ordering(ordering)
                .build();
        return adminCategoryService.registerCategory(registrationDto);
    }

    public void deleteAll() {
        categoryRepository.deleteAll();
    }
}
