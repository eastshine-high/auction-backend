package com.eastshine.auction.common.filters;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.security.UserAuthentication;
import com.eastshine.auction.common.utils.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if(Objects.nonNull(authorization)){
            String accessToken =  authorization.substring("Bearer ".length());
            UserInfo userInfo = jwtUtil.decode(accessToken);
            Authentication authentication = new UserAuthentication(userInfo);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
