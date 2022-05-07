package com.eastshine.auction.user.web;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.web.dto.UserSignupDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User of(UserSignupDto memberSignupDto);
}
