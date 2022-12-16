package com.eastshine.auction.product.repository.category;

import com.eastshine.auction.product.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, CategoryRepositoryCustom {
    
}
