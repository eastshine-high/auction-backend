package com.eastshine.auction.common.utils;

import com.eastshine.auction.common.exception.AuthenticationException;
import com.eastshine.auction.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@SuppressWarnings("unchecked")
@Component
public class JwtUtil {
    public static final String KEY_OF_USER_ID = "userId";

    private final Key signKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        signKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String encode(Long userId) {
        return Jwts.builder()
                .claim(KEY_OF_USER_ID, userId)
                .setIssuedAt(new Date()) // 토큰 발행 일시
                .setExpiration(new Date(System.currentTimeMillis() + (60 * 1000) * 100)) // 토큰 유효 일시(+100분)
                .signWith(signKey)
                .compact();
    }

    public Claims decode(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException(ErrorCode.COMMON_INVALID_TOKEN);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (SignatureException | MalformedJwtException e) {
            throw new AuthenticationException(ErrorCode.COMMON_INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(ErrorCode.COMMON_EXPIRED_TOKEN);
        }
    }
}
