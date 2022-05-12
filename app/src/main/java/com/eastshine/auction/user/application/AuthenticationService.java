package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.utils.JwtUtil;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.web.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public String login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidArgumentException(ErrorCode.AUTH_LOGIN_FAIL));

        if (!user.authenticate(password, passwordEncoder)) {
            throw new InvalidArgumentException(ErrorCode.AUTH_LOGIN_FAIL);
        }

        UserInfo userInfo = UserMapper.INSTANCE.toUserInfo(user);
        return jwtUtil.encode(userInfo);
    }
}
