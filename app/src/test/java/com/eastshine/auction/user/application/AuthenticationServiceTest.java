package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.user.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationServiceTest extends IntegrationTest {
    public static final String REGISTERED_EMAIL = "registered@naver.com";
    public static final String REGISTERED_USER_PW = "validPassword";

    @Autowired private AuthenticationService authenticationService;
    @Autowired private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        userFactory.createUser(REGISTERED_EMAIL, REGISTERED_USER_PW);
    }

    @AfterEach
    void afterEach() {
        userFactory.deleteAllUser();
    }

    @Nested
    class login_메서드는 {

        @Test
        @DisplayName("유효한 회원 정보로 호출 시, JWT 토큰을 반환한다.")
        void loginWithValidInfo(){
            String actual = authenticationService.login(REGISTERED_EMAIL, REGISTERED_USER_PW);

            assertThat(actual.split("\\.").length).isEqualTo(3);
            assertThat(actual.length()).isGreaterThan(30);
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인할 경우, ErrorCode가 AUTH_LOGIN_FAIL인 InvalidArgumentException 예외를 던진다.")
        public void loginWithWrongPassword() {
            try {
                authenticationService.login(REGISTERED_EMAIL, "wrongPassword");

            } catch (InvalidArgumentException ex) {
                assertThat(ex).isInstanceOf(InvalidArgumentException.class);
                assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_LOGIN_FAIL);
            }
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인할 경우, ErrorCode가 AUTH_LOGIN_FAIL인 InvalidArgumentException 예외를 던진다.")
        public void loginWithNotExsistEmail() {
            try {
                authenticationService.login("unregisteredEmail@naver.com", REGISTERED_USER_PW);

            } catch (InvalidArgumentException ex) {
                assertThat(ex).isInstanceOf(InvalidArgumentException.class);
                assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_LOGIN_FAIL);
            }
        }
    }
}
