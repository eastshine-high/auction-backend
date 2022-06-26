package com.eastshine.auction.category.application;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.category.domain.CategoryRepository;
import com.eastshine.auction.category.web.dto.CategoryRegistrationRequest;
import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category registerCategory(CategoryRegistrationRequest categoryRegistrationRequest) {
        Category parentCategory = null;
        if(Objects.nonNull(categoryRegistrationRequest.getParentId())) {
            parentCategory = categoryRepository.findById(categoryRegistrationRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_PARENT_ENTITY_NOT_FOUND));
        }

        Category category = Category.builder()
                .id(categoryRegistrationRequest.getId())
                .parent(parentCategory)
                .ordering(categoryRegistrationRequest.getOrdering())
                .name(categoryRegistrationRequest.getName())
                .build();
        return categoryRepository.save(category);
    }
}
