package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.repository.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AdminCategoryService {
    private final EntityManager em;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category registerCategory(AdminCategoryDto.Request request) {
        Category parentCategory = null;
        if(Objects.nonNull(request.getParentId())) {
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_PARENT_ENTITY_NOT_FOUND));
        }

        return categoryRepository.save(request.toEntity(parentCategory));
    }

    @Transactional(readOnly = true)
    public Category getCategory(Integer id) {
        return categoryRepository.findAdminCategoryById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_ENTITY_NOT_FOUND));
    }

    @Transactional
    public Category modifyCategory(Integer id, AdminCategoryDto.Request request) {
        if(Objects.nonNull(request.getParentId())) {
            categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_PARENT_ENTITY_NOT_FOUND));
        }

        categoryRepository.updateCategoryWithJdbc(id, request);
        return categoryRepository.findAdminCategoryById(request.getId()).orElseThrow();
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
    }

    private Category findCategory(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_ENTITY_NOT_FOUND));
    }
}
