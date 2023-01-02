package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.role.Role;
import com.eastshine.auction.user.repository.UserRepository;
import com.eastshine.auction.user.domain.role.UserRole;
import com.eastshine.auction.user.domain.role.UserRoleId;
import com.eastshine.auction.user.domain.role.RoleType;
import com.eastshine.auction.user.domain.seller.Seller;
import com.eastshine.auction.user.repository.seller.SellerRepository;
import com.eastshine.auction.user.web.dto.SellerDto;
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
        seller.addRole(RoleType.SELLER);
        return seller;
    }

    public SellerDto.Info findSellerInfo(Long id) {
        return sellerRepository.findSellerInfoById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
