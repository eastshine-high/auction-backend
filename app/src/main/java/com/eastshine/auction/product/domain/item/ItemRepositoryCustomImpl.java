package com.eastshine.auction.product.domain.item;

import com.eastshine.auction.product.web.dto.ItemDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.eastshine.auction.product.domain.category.QCategory.category;
import static com.eastshine.auction.product.domain.item.QItem.item;
import static com.eastshine.auction.product.domain.item.option.QItemOption.itemOption;
import static com.eastshine.auction.user.domain.seller.QSeller.seller;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory query;

    public ItemRepositoryCustomImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<ItemDto.SearchResponse> searchItems(ItemDto.SearchCondition condition, Pageable pageable) {
        if(condition.getCategoryId() == null){ // 이 조건이 없는 경우, 성능 최적화를 위해 카테고리를 조인하지 않고 질의
            return findByItemNameContaining(condition, pageable);
        }

        QueryResults<ItemDto.SearchResponse> results = query.select(Projections.constructor(ItemDto.SearchResponse.class,
                        item.id,
                        item.name,
                        item.price,
                        item.shippingFragment.deliveryChargePolicy,
                        item.shippingFragment.deliveryCharge,
                        seller.nickname,
                        seller.sellerLevel))
                .from(category)
                .leftJoin(category).on(item.categoryId.eq(category.id))
                .leftJoin(seller).on(item.createdBy.eq(seller.id))
                .where(
                        category.id.eq(condition.getCategoryId()),
                        item.name.contains(condition.getName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    Page<ItemDto.SearchResponse> findByItemNameContaining(ItemDto.SearchCondition condition, Pageable pageable){
        QueryResults<ItemDto.SearchResponse> results = query.select(Projections.constructor(ItemDto.SearchResponse.class,
                        item.id,
                        item.name,
                        item.price,
                        item.shippingFragment.deliveryChargePolicy,
                        item.shippingFragment.deliveryCharge,
                        seller.nickname,
                        seller.sellerLevel))
                .from(item)
                .join(seller).on(seller.id.eq(item.createdBy))
                .where(
                        item.name.contains(condition.getName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    @Override
    public Optional<Item> findByIdWithFetchJoin(Long itemId) {
        return Optional.ofNullable(
                query.selectFrom(item)
                        .join(item.itemOptions, itemOption).fetchJoin()
                        .where(item.id.eq(itemId))
                        .fetchOne()
        );
    }
}
