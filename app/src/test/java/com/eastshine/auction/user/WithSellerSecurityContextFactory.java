package com.eastshine.auction.user;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.security.UserAuthentication;
import com.eastshine.auction.user.application.SellerService;
import com.eastshine.auction.user.domain.seller.Seller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Random;

public class WithSellerSecurityContextFactory implements WithSecurityContextFactory<WithSeller>  {
    public static final long TEN_DIGITS = 1000000000L;
    public static final Random RANDOM = new Random();

    private final SellerService sellerService;

    public WithSellerSecurityContextFactory(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public SecurityContext createSecurityContext(WithSeller annotation) {
        String nickname = annotation.value();
        Seller seller = Seller.sellerBuilder()
                .email(nickname + "@email.com")
                .password(nickname)
                .nickname(nickname)
                .businessNumber(generateBusinessNumber())
                .build();

        sellerService.signUpSeller(seller);

        Authentication authentication = new UserAuthentication(new UserInfo(seller));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    private String generateBusinessNumber() {
        Long asLong = RANDOM.longs(TEN_DIGITS, TEN_DIGITS * 10)
                .findFirst()
                .getAsLong();
        return asLong.toString();
    }
}
