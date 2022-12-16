package com.eastshine.auction.user.repository.seller;

import com.eastshine.auction.user.domain.seller.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long>, SellerRepositoryCustom{
    boolean existsByBusinessNumber(String businessNumber);
}
