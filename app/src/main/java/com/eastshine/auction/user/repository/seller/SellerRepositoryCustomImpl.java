package com.eastshine.auction.user.repository.seller;

import com.eastshine.auction.user.domain.seller.QSeller;
import com.eastshine.auction.user.web.dto.SellerDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.Optional;

public class SellerRepositoryCustomImpl implements SellerRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public SellerRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<SellerDto.Info> findSellerInfoById(Long id) {
        SellerDto.Info seller = jpaQueryFactory.select(Projections.fields(SellerDto.Info.class,
                        QSeller.seller.id,
                        QSeller.seller.email,
                        QSeller.seller.nickname,
                        QSeller.seller.businessNumber,
                        QSeller.seller.sellerLevel
                ))
                .from(QSeller.seller)
                .where(QSeller.seller.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(seller);
    }
}
