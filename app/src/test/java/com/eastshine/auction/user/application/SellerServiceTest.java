package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.repository.UserRepository;
import com.eastshine.auction.user.domain.role.Role;
import com.eastshine.auction.user.domain.role.RoleId;
import com.eastshine.auction.user.repository.role.RoleRepository;
import com.eastshine.auction.user.domain.role.RoleType;
import com.eastshine.auction.user.domain.seller.Seller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerServiceTest extends IntegrationTest {
    private static final String REGISTERED_EMAIL = "registered@email.com";
    private static final String REGISTERED_NICKNAME = "사용중";
    private static final String REGISTERED_BUSINESS_NUMBER = "1234567890";
    private static final String PASSWORD = "1234";

    @Autowired SellerService sellerService;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Seller seller = sellerService.signUpSeller(
                Seller.sellerBuilder()
                        .nickname(REGISTERED_NICKNAME)
                        .email(REGISTERED_EMAIL)
                        .password(PASSWORD)
                        .businessNumber(REGISTERED_BUSINESS_NUMBER)
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Nested
    @DisplayName("signUpSeller 메소드는")
    class signUpSeller {
        private Seller signupInfo;

        @Nested
        @DisplayName("이메일과 닉네임이 중복되지 않은 경우")
        class Context_with_not_exsisted_email{
            private String newEmail = "new@email.com";
            private String newNickname = "새로운";
            private String businessNumber = "0123456789";

            @BeforeEach
            void setUp() {
                signupInfo = Seller.sellerBuilder()
                        .nickname(newNickname)
                        .email(newEmail)
                        .password(PASSWORD)
                        .businessNumber(businessNumber)
                        .build();
            }

            @DisplayName("가입한 회원 정보를 반환한다.")
            @Test
            void it_returns_signedUpUser() {
                Seller signedUpSeller = sellerService.signUpSeller(signupInfo);

                assertThat(signedUpSeller.getNickname()).isEqualTo(newNickname);
                assertThat(signedUpSeller.getEmail()).isEqualTo(newEmail);
                assertThat(passwordEncoder.matches(PASSWORD, signedUpSeller.getPassword())).isTrue();
                assertThat(signedUpSeller.getStatus()).isEqualTo(User.Status.ACTIVE);
                assertThat(signedUpSeller.getBusinessNumber()).isEqualTo(businessNumber);
                assertThat(signedUpSeller.getRoles()).contains(new Role(new RoleId(signedUpSeller, RoleType.SELLER)));
            }
        }
        @Nested
        @DisplayName("이메일이 중복된 경우")
        class Context_with_exsisted_email{

            @BeforeEach
            void setUp() {
                signupInfo = Seller.sellerBuilder()
                        .email(REGISTERED_EMAIL)
                        .nickname("newNickname")
                        .password("other_password")
                        .businessNumber("1234567890")
                        .build();
            }

            @Test
            @DisplayName("ErrorCode가 USER_DUPLICATE_EMAIL인 예외를 던진다.")
            void it_throws_IllegalArgumentException() {
                assertThatThrownBy(
                        () -> sellerService.signUpSeller(signupInfo)
                )
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.USER_DUPLICATE_EMAIL.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("닉네임이 중복된 경우")
        class Context_with_exsisted_nickname{
            private String newEmail = "new@email.com";

            @BeforeEach
            void setUp() {
                signupInfo = Seller.sellerBuilder()
                        .nickname(REGISTERED_NICKNAME)
                        .email(newEmail)
                        .password("other_password")
                        .businessNumber("1234567890")
                        .build();
            }

            @Test
            @DisplayName("ErrorCode가 USER_DUPLICATE_NICKNAME인 예외를 던진다.")
            void it_throws_IllegalArgumentException() {
                assertThatThrownBy(
                        () -> sellerService.signUpSeller(signupInfo)
                )
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.USER_DUPLICATE_NICKNAME.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("사업자 번호가 중복된 경우")
        class Context_with_exsisted_business_number{
            private String newEmail = "new@email.com";
            private String newNickname = "newNickname";

            @BeforeEach
            void setUp() {
                signupInfo = Seller.sellerBuilder()
                        .nickname(newNickname)
                        .email(newEmail)
                        .password("other_password")
                        .businessNumber(REGISTERED_BUSINESS_NUMBER)
                        .build();
            }

            @Test
            @DisplayName("ErrorCode가 USER_DUPLICATE_BUSINESS_NUMBER인 예외를 던진다.")
            void it_throws_IllegalArgumentException() {
                assertThatThrownBy(
                        () -> sellerService.signUpSeller(signupInfo)
                )
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.USER_DUPLICATE_BUSINESS_NUMBER.getErrorMsg());
            }
        }
    }
}

