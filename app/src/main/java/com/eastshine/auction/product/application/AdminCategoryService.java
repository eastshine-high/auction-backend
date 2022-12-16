package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.repository.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category registerCategory(AdminCategoryDto.RegistrationRequest registrationRequest) {
        Category parentCategory = null;
        if(Objects.nonNull(registrationRequest.getParentId())) {
            parentCategory = categoryRepository.findById(registrationRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_PARENT_ENTITY_NOT_FOUND));
        }

        return categoryRepository.save(registrationRequest.toEntity(parentCategory));
    }
}
