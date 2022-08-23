package com.eastshine.auction.user.domain.seller;

import com.eastshine.auction.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * 판매자
 */
@Getter
@NoArgsConstructor
@Entity
public class Seller extends User {

    @Column(unique = true, nullable = false)
    private String businessNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SellerLevelType sellerLevel = SellerLevelType.NEW;

    @Builder(builderMethodName = "sellerBuilder")
    public Seller(String email, String password, String nickname, String businessNumber) {
        super(email, password, nickname);
        this.businessNumber = businessNumber;
    }
}
