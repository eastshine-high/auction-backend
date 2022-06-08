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

    default UserInfo toUserInfo(User user){
        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .roles(user.getRoles().stream()
                        .map(role -> role.getRoleId().getRole())
                        .collect(Collectors.toList()))
                .build();
    };
}
