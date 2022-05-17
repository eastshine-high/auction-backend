package com.eastshine.auction.common.security;

import com.eastshine.auction.common.model.UserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final UserInfo userInfo;

    public UserAuthentication(UserInfo userInfo) {
        super(authorities(userInfo));
        this.userInfo = userInfo;
    }

    private static List<GrantedAuthority> authorities(UserInfo userInfo) {
        return userInfo.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
