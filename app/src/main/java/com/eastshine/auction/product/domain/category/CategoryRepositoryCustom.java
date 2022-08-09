package com.eastshine.auction.product.domain.category;

import java.util.List;

public interface CategoryRepositoryCustom {

    public List<Category> findDisplayCategories();
}
