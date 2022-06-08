package com.eastshine.auction.common.utils;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.model.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Maps;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {
    public static final String KEY_OF_USER_INFO = "userInfo";

    private final Key signKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        signKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String encode(UserInfo userInfo) {
        return Jwts.builder()
                .claim(KEY_OF_USER_INFO, userInfo)
                .signWith(signKey)
                .compact();
    }

    public UserInfo decode(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidArgumentException(token, ErrorCode.AUTH_INVALID_TOKEN);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .deserializeJsonWith(new JacksonDeserializer(Maps.of(KEY_OF_USER_INFO, UserInfo.class).build()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get(KEY_OF_USER_INFO, UserInfo.class);

        } catch (SignatureException e) {
            throw new InvalidArgumentException(token, ErrorCode.AUTH_INVALID_TOKEN);
        }
    }
}
