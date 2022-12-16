package com.eastshine.auction.product.repository.category;

import com.eastshine.auction.product.domain.category.Category;

import java.util.List;

public interface CategoryRepositoryCustom {

    public List<Category> findDisplayCategories();
}
