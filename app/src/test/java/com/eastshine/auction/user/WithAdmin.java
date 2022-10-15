package com.eastshine.auction.user;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAdminSecurityContextFactory.class)
public @interface WithAdmin {

    String value();
}
