package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.AuthenticationException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.model.UserInfoRedisRepository;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.user.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticationServiceTest extends IntegrationTest {
    public static final String REGISTERED_EMAIL = "registered@naver.com";
    public static final String REGISTERED_USER_PW = "validPassword";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0" + "invalidToken";

    @Autowired private AuthenticationService authenticationService;
    @Autowired private UserFactory userFactory;
    @Autowired private UserInfoRedisRepository userInfoRedisRepository;

    @BeforeEach
    void setUp() {
        userFactory.createUser(REGISTERED_EMAIL, REGISTERED_USER_PW);
    }

    @AfterEach
    void afterEach() {
        userFactory.deleteAll();
        userInfoRedisRepository.deleteAll();
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

    @Nested
    class parseToken_메서드는 {

        @Test
        void parseTokenWithValidToken() {
            Long userId = authenticationService.parseToken(VALID_TOKEN);

            assertThat(userId).isEqualTo(1L);
        }

        @Test
        void parseTokenWithInvalidToken() {
            assertThatThrownBy(
                    () -> authenticationService.parseToken(INVALID_TOKEN)
            ).isInstanceOf(AuthenticationException.class);
        }
    }

    @Nested
    class findUserInfo_메소드는{

        @Test
        @DisplayName("등록된 사용자 정보 식별자로 조회할 경우, 사용자 정보를 반환합니다.")
        void findUserInfoWithRegisteredId() {
            long registeredId = 1L;
            UserInfo userInfo = UserInfo.builder()
                    .id(registeredId)
                    .nickname("nickname")
                    .build();
            userInfoRedisRepository.save(userInfo);

            UserInfo actual = authenticationService.findUserInfo(registeredId);

            assertThat(actual.getId()).isEqualTo(registeredId);
            assertThat(actual.getNickname()).isEqualTo("nickname");
        }

        @Test
        @DisplayName("등록되지 않은 사용자 정보 식별자로 조회할 경우, 사용자 정보를 반환합니다.")
        void findUserInfoWithNotRegisteredId() {
            long notRegisteredId = 999999L;
            UserInfo userInfo = UserInfo.builder()
                    .id(1L)
                    .nickname("nickname")
                    .build();
            userInfoRedisRepository.save(userInfo);

            assertThatThrownBy(() ->
                    authenticationService.findUserInfo(notRegisteredId)
            )
                    .isInstanceOf(AuthenticationException.class);
        }
    }
}
