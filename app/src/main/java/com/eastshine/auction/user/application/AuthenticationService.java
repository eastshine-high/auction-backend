package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.AuthenticationException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.model.UserInfoRedisRepository;
import com.eastshine.auction.common.utils.JwtUtil;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserInfoRedisRepository userInfoRedisRepository;

    @Transactional(readOnly = true)
    public String login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidArgumentException(ErrorCode.USER_LOGIN_FAIL));

        if (!user.authenticate(password, passwordEncoder)) {
            throw new InvalidArgumentException(ErrorCode.USER_LOGIN_FAIL);
        }

        userInfoRedisRepository.save(user.toUserInfo());
        return jwtUtil.encode(user.getId());
    }

    public Long parseToken(String accessToken) {
        Claims claims = jwtUtil.decode(accessToken);
        return claims.get(JwtUtil.KEY_OF_USER_ID, Long.class);
    }

    public UserInfo findUserInfo(Long userId) {
        return userInfoRedisRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.COMMON_INVALID_TOKEN));
    }
}
