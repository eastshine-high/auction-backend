package com.eastshine.auction.user.domain;

import com.eastshine.auction.user.domain.seller.Seller;
import com.eastshine.auction.user.web.dto.SellerDto;
import com.eastshine.auction.user.web.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User of(UserDto.Signup userSignupDto);

    Seller of(SellerDto.Signup sellerSignupDto);
}
