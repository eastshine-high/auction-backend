package com.eastshine.auction.user;

import com.eastshine.auction.user.application.SellerService;
import com.eastshine.auction.user.application.UserService;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.repository.UserRepository;
import com.eastshine.auction.user.repository.role.RoleRepository;
import com.eastshine.auction.user.domain.seller.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class UserFactory {
    public static final long TEN_DIGITS = 1000000000L;
    public static final Random RANDOM = new Random();

    @Autowired UserService userService;
    @Autowired SellerService sellerService;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    public User createUser(String email, String password) {
        return createUser(
                email,
                "nickname" + UUID.randomUUID(),
                password
        );
    }

    public User createUser(String email, String nickname, String password) {
        return userService.signUpUser(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(password)
                        .build()
        );
    }

    public Seller createSeller(String nickname) {
        Seller seller = Seller.sellerBuilder()
                .email(nickname + "@email.com")
                .nickname(nickname)
                .password(nickname)
                .businessNumber(generateBusinessNumber())
                .build();
        return sellerService.signUpSeller(seller);
    }

    private String generateBusinessNumber() {
        Long asLong = RANDOM.longs(TEN_DIGITS, TEN_DIGITS * 10)
                .findFirst()
                .getAsLong();
        return asLong.toString();
    }

    public void deleteAll() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
