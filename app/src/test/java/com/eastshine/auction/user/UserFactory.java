package com.eastshine.auction.user;

import com.eastshine.auction.user.application.UserService;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.domain.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserFactory {
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    public User createUser(String email, String password) {
        return createUser(
                email,
                "nickname" + UUID.randomUUID(),
                password
        );
    }

    public User createUser(String email, String nickname, String password) {
        return userService.signUpUser(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(password)
                        .build()
        );
    }

    public void deleteAllUser() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
