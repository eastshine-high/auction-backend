package com.eastshine.auction.product.domain;

import com.eastshine.auction.product.web.dto.ProductRegistrationRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.eastshine.auction.product.domain.QProduct.product;
import static com.eastshine.auction.product.domain.category.QProductCategory.productCategory;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    public Boolean existsProduct(ProductRegistrationRequest registrationRequest) {
        Product fetchOne = jpaQueryFactory
                .selectFrom(product)
                .join(product.productCategories, productCategory)
                .where(productCategory.productCategoryId.category.id.eq(registrationRequest.getCategoryId()),
                        product.name.eq(registrationRequest.getName()))
                .fetchFirst();

        return fetchOne != null;
    }
}
