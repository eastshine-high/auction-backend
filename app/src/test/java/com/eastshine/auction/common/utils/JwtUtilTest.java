package com.eastshine.auction.common.utils;

import com.eastshine.auction.common.exception.AuthenticationException;
import com.eastshine.auction.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789012";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjEsImlhdCI6MTY2Nzk5Nzg5NSwiZXhwIjo2Mzc1MDIwNzYwMH0." +
            "2-b-RTQJTUeOWrD0IgaTlRN_W1p1plLCu40DAAMb4-w";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9." +
            "ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk" + "invalidxxxxxxx";
    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjEsImlhdCI6MTY2NzgwNDA1NSwiZXhwIjoxNjY3ODEwMDU1fQ." +
            "84gTTHteTKnJHcUduGwmqyKMmenHZ10HDzBzg3awyKk";

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

        assertThat(token.split("\\.")).hasSize(3);
        assertThat(token.split("\\.")[0]).isEqualTo("eyJhbGciOiJIUzI1NiJ9");
    }

    @Test
    void decodeWithValidToken() {
        Claims claims = jwtUtil.decode(VALID_TOKEN);

        assertThat(claims.get(JwtUtil.KEY_OF_USER_ID, Long.class)).isEqualTo(userId);
    }

    @Test
    void decodeWithExpiredToken() {
        assertThatThrownBy(() -> jwtUtil.decode(EXPIRED_TOKEN))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(ErrorCode.COMMON_EXPIRED_TOKEN.getErrorMsg());
    }

    @ParameterizedTest
    @ValueSource(strings = {INVALID_TOKEN, "    "})
    @NullAndEmptySource
    void decodeWithInvalidToken(String whiteSpace) {
        assertThatThrownBy(() -> jwtUtil.decode(whiteSpace))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(ErrorCode.COMMON_INVALID_TOKEN.getErrorMsg());
    }
}
