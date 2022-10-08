package com.eastshine.auction.product.domain.product;

import com.eastshine.auction.product.web.dto.ProductDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import static com.eastshine.auction.product.domain.category.QCategory.category;
import static com.eastshine.auction.product.domain.product.QProduct.product;
import static com.eastshine.auction.product.domain.product.option.QProductOption.productOption;
import static com.eastshine.auction.user.domain.seller.QSeller.seller;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<ProductDto.SearchResponse> searchProducts(ProductDto.SearchCondition condition, Pageable pageable) {
        if(condition.getCategoryId() == null){ // 이 조건이 없는 경우, 성능 최적화를 위해 카테고리를 조인하지 않고 질의
            return findByProductNameContaining(condition, pageable);
        }

        QueryResults<ProductDto.SearchResponse> results = jpaQueryFactory.select(Projections.fields(ProductDto.SearchResponse.class,
                        product.id,
                        product.name,
                        product.price,
                        seller.nickname,
                        seller.sellerLevel))
                .from(category)
                .leftJoin(category).on(product.categoryId.eq(category.id))
                .leftJoin(seller).on(product.createdBy.eq(seller.id))
                .where(
                        category.id.eq(condition.getCategoryId()),
                        product.name.contains(condition.getName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    Page<ProductDto.SearchResponse> findByProductNameContaining(ProductDto.SearchCondition condition, Pageable pageable){
        QueryResults<ProductDto.SearchResponse> results = jpaQueryFactory.select(Projections.fields(ProductDto.SearchResponse.class,
                        product.id,
                        product.name,
                        product.price,
                        seller.nickname,
                        seller.sellerLevel))
                .from(product)
                .join(seller).on(seller.id.eq(product.createdBy))
                .where(
                        product.name.contains(condition.getName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    @Override
    public ProductDto.Info findGuestProductInfo(Long id){
        return jpaQueryFactory.select(Projections.fields(ProductDto.Info.class,
                        product.id,
                        product.name,
                        product.price,
                        product.stockQuantity,
                        product.productOptionsTitle,
                        product.updatedAt,
                        seller.nickname,
                        seller.sellerLevel))
                .from(product)
                .join(seller).on(seller.id.eq(product.createdBy))
                .where(
                        product.id.eq(id)
                )
                .fetchOne();
    }
}
