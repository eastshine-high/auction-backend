package com.eastshine.auction.product.repository.category;

import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.domain.category.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public CategoryRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Category> findDisplayCategories() {
        QCategory category = new QCategory("category");
        QCategory childCategory = new QCategory("childCategory");

        return jpaQueryFactory.selectFrom(category)
                .distinct()
                .leftJoin(category.children, childCategory)
                .fetchJoin()
                .where(
                        category.parent.isNull()
                )
                .orderBy(category.ordering.asc(), childCategory.ordering.asc())
                .fetch();
    }
}
