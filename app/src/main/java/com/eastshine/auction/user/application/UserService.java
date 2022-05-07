package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.domain.role.Role;
import com.eastshine.auction.user.domain.role.RoleId;
import com.eastshine.auction.user.domain.role.RoleRepository;
import com.eastshine.auction.user.domain.role.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * 사용자를 등록하고 권한을 생성한 뒤, 등록된 사용자 정보를 반환합니다.
     *
     * @param signupInfo 등록할 사용자 정보.
     * @return 등록된 사용자 정보.
     * @Throw IllegalArgumentException 1 회원 가입할 이메일 정보가 사용 중인 경우. 2 닉네임 정보가 사용 중인 경우.
     */
    @Transactional
    public User signUpUser(User signupInfo) {
        if(userRepository.existsByEmail(signupInfo.getEmail())){
            throw new InvalidArgumentException(ErrorCode.USER_DUPLICATE_EMAIL);
        }
        if(userRepository.existsByNickname(signupInfo.getNickname())){
            throw new InvalidArgumentException(ErrorCode.USER_DUPLICATE_NICKNAME);
        }

        User user = userRepository.save(signupInfo);
        Role userRole = new Role(new RoleId(user, RoleType.USER));
        roleRepository.save(userRole);
        return user;
    }
}
