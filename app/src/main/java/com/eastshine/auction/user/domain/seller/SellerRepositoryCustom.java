package com.eastshine.auction.user.domain.seller;

import com.eastshine.auction.user.web.dto.SellerDto;

import java.util.Optional;

public interface SellerRepositoryCustom {
    Optional<SellerDto.Info> findSellerInfoById(Long id);
}
