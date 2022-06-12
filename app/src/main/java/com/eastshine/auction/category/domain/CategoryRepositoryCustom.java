package com.eastshine.auction.category.domain;

import com.eastshine.auction.category.web.dto.MainDisplayCategoryDto;

import java.util.List;

public interface CategoryRepositoryCustom {

    public List<Category> findDisplayCategories();
}
