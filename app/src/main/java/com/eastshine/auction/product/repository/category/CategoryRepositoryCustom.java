package com.eastshine.auction.product.repository.category;

import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryCustom {

    List<Category> findDisplayCategories();

    Optional<Category> findAdminCategoryById(Integer id);

    int updateCategoryWithJdbc(Integer id, AdminCategoryDto.Request request);
}
