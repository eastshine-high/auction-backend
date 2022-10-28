package com.eastshine.auction.user;

import com.eastshine.auction.common.security.UserAuthentication;
import com.eastshine.auction.user.application.UserService;
import com.eastshine.auction.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithUserSecurityContextFactory implements WithSecurityContextFactory<WithUser>  {
    private final UserService userService;

    public WithUserSecurityContextFactory(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SecurityContext createSecurityContext(WithUser annotation) {
        String nickname = annotation.value();
        User user = User.builder()
                .email(nickname + "@email.com")
                .password(nickname)
                .nickname(nickname)
                .build();

        userService.signUpUser(user);

        Authentication authentication = new UserAuthentication(user.toUserInfo());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
