package com.eastshine.auction.user.repository;

import com.eastshine.auction.user.web.dto.UserDto;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<UserDto.Info> findUserInfoById(Long id);
}
