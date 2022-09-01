package com.eastshine.auction.common.utils;

import com.eastshine.auction.common.exception.AuthenticationException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.user.domain.role.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJ1c2VySW5mbyI6eyJpZCI6MSwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsIm5pY2tuYW1lIjoibmlja25hbWUiLCJyb2xlcyI6WyJVU0VSIiwiQURNSU4iXX19" +
            ".2s3sfdLWmcdyT4FeXz8wzKeODyPmxkLHJSF8jmGwOPI";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0" + "invalidxxxxxxx";

    private JwtUtil jwtUtil;
    private UserInfo userInfo;

    @BeforeEach
    void setUp(){
        jwtUtil = new JwtUtil(SECRET);
        userInfo = UserInfo.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("nickname")
                .roles(Arrays.asList(RoleType.USER, RoleType.ADMIN))
                .build();
    }

    @Test
    void encode() {
        String token = jwtUtil.encode(userInfo);

        assertThat(token).isEqualTo(VALID_TOKEN);
    }

    @Test
    void decodeWithValidToken() {
        UserInfo decodedToken = jwtUtil.decode(VALID_TOKEN);

        assertThat(decodedToken).isEqualTo(userInfo);
    }

    @ParameterizedTest
    @ValueSource(strings = {INVALID_TOKEN, "    "})
    @NullAndEmptySource
    void decodeWithInvalidToken(String whiteSpace) {
        assertThatThrownBy(() -> jwtUtil.decode(whiteSpace))
                .isInstanceOf(AuthenticationException.class);
    }
}
