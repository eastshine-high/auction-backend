package com.eastshine.auction.common.utils;

import com.eastshine.auction.common.exception.AuthenticationException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.user.domain.role.RoleType;
import io.jsonwebtoken.Claims;
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
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9." +
            "ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9." +
            "ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk" + "invalidxxxxxxx";

    private JwtUtil jwtUtil;
    private Long userId;

    @BeforeEach
    void setUp(){
        jwtUtil = new JwtUtil(SECRET);
        userId = 1L;
    }

    @Test
    void encode() {
        String token = jwtUtil.encode(userId);

        assertThat(token).isEqualTo(VALID_TOKEN);
    }

    @Test
    void decodeWithValidToken() {
        Claims claims = jwtUtil.decode(VALID_TOKEN);

        assertThat(claims.get(JwtUtil.KEY_OF_USER_ID, Long.class)).isEqualTo(userId);
    }

    @ParameterizedTest
    @ValueSource(strings = {INVALID_TOKEN, "    "})
    @NullAndEmptySource
    void decodeWithInvalidToken(String whiteSpace) {
        assertThatThrownBy(() -> jwtUtil.decode(whiteSpace))
                .isInstanceOf(AuthenticationException.class);
    }
}
