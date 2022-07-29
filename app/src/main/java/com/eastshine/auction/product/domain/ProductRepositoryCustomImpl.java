package com.eastshine.auction.product.domain;

import com.eastshine.auction.product.web.dto.ProductDto;
import com.eastshine.auction.product.web.dto.ProductSearchCondition;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import static com.eastshine.auction.product.domain.QProduct.product;
import static com.eastshine.auction.product.domain.category.QProductCategory.productCategory;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<ProductDto> findProducts(ProductSearchCondition condition, Pageable pageable) {
        if(condition.getCategoryId() == null){ // 이 조건이 없는 경우, 조인 없이 질의
            return findByProductNameContaining(condition, pageable);
        }

        QueryResults<ProductDto> results = jpaQueryFactory.select(Projections.fields(ProductDto.class,
                        product.id,
                        product.name,
                        product.price))
                .from(productCategory)
                .leftJoin(productCategory.productCategoryId.product, product)
                .where(
                        productCategory.productCategoryId.category.id.eq(condition.getCategoryId()),
                        product.name.contains(condition.getName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    Page<ProductDto> findByProductNameContaining(ProductSearchCondition condition, Pageable pageable){
        QueryResults<ProductDto> results = jpaQueryFactory.select(Projections.fields(ProductDto.class,
                        product.id,
                        product.name,
                        product.price))
                .from(product)
                .where(product.name.contains(condition.getName()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        long total = results.getTotal();
        return new PageImpl<>(results.getResults(), pageable, total);
    }

    public Boolean existsProduct(Integer categoryId, String productName) {
        Product fetchOne = jpaQueryFactory
                .selectFrom(product)
                .join(product.productCategories, productCategory)
                .where(
                        productCategory.productCategoryId.category.id.eq(categoryId),
                        product.name.eq(productName))
                .fetchFirst();

        return fetchOne != null;
    }
}
