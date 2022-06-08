package com.eastshine.auction.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Nested
    class authenticate_메소드는{

        @DisplayName("회원 상태가 활동중이고 비밀번호가 일치하는 경우, True를 반환한다.")
        @Test
        void authenticate() {
            String password = "password";
            User user = User.builder()
                    .password(password)
                    .build();
            user.encryptPassword(passwordEncoder);
            ReflectionTestUtils.setField(user, "status", User.Status.ACTIVE);

            assertThat(
                    user.authenticate(password, passwordEncoder)).isTrue();
        }

        @DisplayName("회원 상태가 활동중이 아닌 경우, False를 반환한다.")
        @Test
        void authenticateWithNotActiveUser() {
            String password = "password";
            User user = User.builder()
                    .password(password)
                    .build();
            user.encryptPassword(passwordEncoder);
            ReflectionTestUtils.setField(user, "status", User.Status.DROPOUT);

            assertThat(
                    user.authenticate(password, passwordEncoder)).isFalse();
        }

        @DisplayName("잘못된 비밀번호로 인증하는 경우, False를 반환한다.")
        @Test
        void authenticateWithInvalidPassword() {
            String password = "password";
            User user = User.builder()
                    .password(password)
                    .build();
            user.encryptPassword(passwordEncoder);
            ReflectionTestUtils.setField(user, "status", User.Status.DROPOUT);

            assertThat(
                    user.authenticate("invalidPassword", passwordEncoder)).isFalse();
        }
    }
}
