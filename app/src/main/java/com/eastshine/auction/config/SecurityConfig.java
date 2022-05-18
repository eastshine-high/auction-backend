package com.eastshine.auction.config;

import com.eastshine.auction.common.filters.JwtAuthenticationErrorFilter;
import com.eastshine.auction.common.filters.JwtAuthenticationFilter;
import com.eastshine.auction.common.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), jwtUtil);
        Filter authenticationErrorFilter = new JwtAuthenticationErrorFilter();

        http
                .csrf().disable()
                .headers()
                    .frameOptions().disable() // H2 데이터베이스 콘솔을 위한 설정.
                .and()
                .addFilter(authenticationFilter)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class);
    }
}
