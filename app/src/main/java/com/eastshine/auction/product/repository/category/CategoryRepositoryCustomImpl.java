package com.eastshine.auction.product.repository.category;

import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.domain.category.QCategory;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final JdbcTemplate jdbcTemplate;

    public CategoryRepositoryCustomImpl(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
        this.jdbcTemplate = jdbcTemplate;
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

    @Override
    public Optional findAdminCategoryById(Integer id) {
        QCategory category = new QCategory("category");
        QCategory childCategory = new QCategory("childCategory");

        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(category)
                        .leftJoin(category.children, childCategory).fetchJoin()
                        .where(
                                category.id.eq(id)
                        )
                        .orderBy(childCategory.ordering.asc())
                        .fetchOne()
        );
    }

    @Override
    public int updateCategoryWithJdbc(Integer id, AdminCategoryDto.Request request) {
        return this.jdbcTemplate.update(
                "update category set category_id = ?, parent_id = ?, name = ?, ordering = ? where category_id = ?",
                request.getId(), request.getParentId(), request.getName(), request.getOrdering(), id);
    }
}
