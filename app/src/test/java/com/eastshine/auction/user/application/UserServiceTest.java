package com.eastshine.auction.user.application;

import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.domain.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private static final String REGISTERED_EMAIL ="registered@email.com";
    private static final String REGISTERED_NICKNAME ="사용중";
    private static final String PASSWORD ="1234";

    @BeforeEach
    void setUpEach() {
        User user = userService.signUpUser(
                User.builder()
                        .nickname(REGISTERED_NICKNAME)
                        .email(REGISTERED_EMAIL)
                        .password(PASSWORD)
                        .build()
        );
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

    }

    @Nested
    @DisplayName("회원 가입시에")
    class Describe_signUpUser {
        private User signupInfo;

        @Nested
        @DisplayName("이메일과 닉네임이 중복되지 않은 경우")
        class Context_with_not_exsisted_email{
            private String newEmail = "new@email.com";
            private String newNickname = "새로운";

            @BeforeEach
            void setUp() {
                signupInfo = User.builder()
                        .nickname(newNickname)
                        .email(newEmail)
                        .password(PASSWORD)
                        .build();
            }

            @DisplayName("가입한 회원 정보를 반환한다.")
            @Test
            void it_returns_signedUpUser() {
                User signedUpUser = userService.signUpUser(signupInfo);
                signedUpUser = userRepository.findById(signedUpUser.getId()).orElse(new User());

                assertThat(signedUpUser.getNickname()).isEqualTo(newNickname);
                assertThat(signedUpUser.getEmail()).isEqualTo(newEmail);
                assertThat(signedUpUser.getPassword()).isEqualTo(PASSWORD);
                assertThat(signedUpUser.getStatus()).isEqualTo(User.Status.SINGUP);
            }
        }

        @Nested
        @DisplayName("이메일이 중복된 경우")
        class Context_with_exsisted_email{

            @BeforeEach
            void setUp() {
                signupInfo = User.builder()
                        .email(REGISTERED_EMAIL)
                        .password("other_password")
                        .build();
            }

            @Test
            @DisplayName("ErrorCode가 USER_DUPLICATE_EMAIL인 예외를 던진다.")
            void it_throws_IllegalArgumentException() {
                try {
                    userService.signUpUser(signupInfo);
                }
                catch (InvalidArgumentException e) {
                    assertThat(e.getErrorCode()).isEqualTo(ErrorCode.USER_DUPLICATE_EMAIL);
                }
            }
        }

        @Nested
        @DisplayName("닉네임이 중복된 경우")
        class Context_with_exsisted_nickname{
            private String newEmail = "new@email.com";

            @BeforeEach
            void setUp() {
                signupInfo = User.builder()
                        .nickname(REGISTERED_NICKNAME)
                        .email(newEmail)
                        .password("other_password")
                        .build();
            }

            @Test
            @DisplayName("ErrorCode가 USER_DUPLICATE_NICKNAME인 예외를 던진다.")
            void it_throws_IllegalArgumentException() {
                try {
                    userService.signUpUser(signupInfo);
                }
                catch (InvalidArgumentException e) {
                    assertThat(e.getErrorCode()).isEqualTo(ErrorCode.USER_DUPLICATE_NICKNAME);
                }
            }
        }
    }


}
