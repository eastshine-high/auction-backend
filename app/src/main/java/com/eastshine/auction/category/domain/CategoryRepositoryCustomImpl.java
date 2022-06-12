package com.eastshine.auction.category.domain;

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
        QCategory parent = new QCategory("parent");
        QCategory child = new QCategory("child");

        return jpaQueryFactory.selectFrom(parent)
                .distinct()
                .leftJoin(parent.children, child)
                .fetchJoin()
                .where(
                        parent.parentId.isNull()
                )
                .orderBy(parent.ordering.asc(), child.ordering.asc())
                .fetch();
    }
}
