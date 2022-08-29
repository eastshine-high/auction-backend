package com.eastshine.auction.user.domain;

import com.eastshine.auction.user.web.dto.UserDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.Optional;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public UserRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<UserDto.Info> findUserInfoById(Long id) {
        UserDto.Info user = jpaQueryFactory.select(Projections.fields(UserDto.Info.class,
                        QUser.user.id,
                        QUser.user.email,
                        QUser.user.nickname
                ))
                .from(QUser.user)
                .where(QUser.user.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(user);
    }
}
