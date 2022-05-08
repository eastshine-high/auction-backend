package com.eastshine.auction.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void encryptPassword() {
        String password = "password";
        User user = User.builder()
                .password(password)
                .build();

        user.encryptPassword(passwordEncoder);

        assertThat(user.getPassword()).isNotEqualTo(password);
        assertThat(user.getPassword()).isNotEmpty();
    }
}
