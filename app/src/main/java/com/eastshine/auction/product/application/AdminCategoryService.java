package com.eastshine.auction.product.application;

import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.domain.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.AdminCategoryRegistrationRequest;
import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category registerCategory(AdminCategoryRegistrationRequest adminCategoryRegistrationRequest) {
        Category parentCategory = null;
        if(Objects.nonNull(adminCategoryRegistrationRequest.getParentId())) {
            parentCategory = categoryRepository.findById(adminCategoryRegistrationRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_PARENT_ENTITY_NOT_FOUND));
        }

        return categoryRepository.save(adminCategoryRegistrationRequest.toEntity(parentCategory));
    }
}
