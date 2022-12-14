package com.eastshine.auction.common.security;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.security.UserAuthentication;
import com.eastshine.auction.user.application.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * JWT 토큰에 대한 인증을 처리
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if(Objects.nonNull(authorization)){
            String accessToken =  authorization.substring("Bearer ".length());
            Long userId = authenticationService.parseToken(accessToken);
            UserInfo userInfo = authenticationService.findUserInfo(userId);
            Authentication authentication = new UserAuthentication(userInfo);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
