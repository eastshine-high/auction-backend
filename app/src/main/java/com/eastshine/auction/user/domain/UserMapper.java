package com.eastshine.auction.user.domain;

import com.eastshine.auction.user.domain.seller.Seller;
import com.eastshine.auction.user.web.dto.SellerDto;
import com.eastshine.auction.user.web.dto.SellerSignupDto;
import com.eastshine.auction.user.web.dto.UserSignupDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    Seller of(SellerDto.Signup sellerSignupDto);

    User of(UserSignupDto memberSignupDto);
}
