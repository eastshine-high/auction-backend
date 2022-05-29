package com.eastshine.auction.config;

import com.eastshine.auction.common.model.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(authentication -> isNotAnonymousUser(authentication))
                .map(Authentication::getPrincipal)
                .map(UserInfo.class::cast)
                .map(UserInfo::getId);
    }

    private boolean isNotAnonymousUser(Authentication authentication) {
        return !(authentication instanceof AnonymousAuthenticationToken);
    }
}
