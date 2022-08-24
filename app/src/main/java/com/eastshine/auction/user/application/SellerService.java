package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.domain.role.Role;
import com.eastshine.auction.user.domain.role.RoleId;
import com.eastshine.auction.user.domain.role.RoleType;
import com.eastshine.auction.user.domain.seller.Seller;
import com.eastshine.auction.user.domain.seller.SellerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerService {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    public SellerService(UserRepository userRepository, SellerRepository sellerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 판매자를 등록하고 권한을 생성한 뒤, 등록된 판매자 정보를 반환합니다.
     *
     * @param seller 등록할 판매자 정보.
     * @return 등록된 판매자 정보.
     * @Throw IllegalArgumentException 1 회원 가입할 이메일 정보가 사용 중인 경우.
     *                                 2 닉네임 정보가 사용 중인 경우.
     */
    @Transactional
    public Seller signUpSeller(Seller seller) {
        if(userRepository.existsByEmail(seller.getEmail())){
            throw new InvalidArgumentException(ErrorCode.USER_DUPLICATE_EMAIL);
        }
        if(userRepository.existsByNickname(seller.getNickname())){
            throw new InvalidArgumentException(ErrorCode.USER_DUPLICATE_NICKNAME);
        }
        if(sellerRepository.existsByBusinessNumber(seller.getBusinessNumber())){
            throw new InvalidArgumentException(ErrorCode.USER_DUPLICATE_BUSINESS_NUMBER);
        }

        seller.encryptPassword(passwordEncoder);
        sellerRepository.save(seller);
        addSellerRole(seller);
        return seller;
    }

    private void addSellerRole(User seller) {
        Role sellerRole = new Role(new RoleId(seller, RoleType.SELLER));
        sellerRole.setSelfCreation();
        seller.addRole(sellerRole);
    }
}
