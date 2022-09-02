package com.eastshine.auction.user.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.web.dto.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        signupInfo.encryptPassword(passwordEncoder);
        return userRepository.save(signupInfo);
    }

    public UserDto.Info findUserInfo(Long id) {
        return userRepository.findUserInfoById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public User updateNickname(Long id, String nickname, Long accessorId) {
        if(!id.equals(accessorId)){
            throw new UnauthorizedException(ErrorCode.USER_INACCESSIBLE);
        }
        if(userRepository.existsByNickname(nickname)){
            throw new InvalidArgumentException(ErrorCode.USER_DUPLICATE_NICKNAME);
        }
        User user = findUser(id);
        user.updateNickname(nickname);
        return user;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void deleteUser(Long id, Long accessorId) {
        validateAccessibleUser(id, accessorId);
        if(!userRepository.existsById(id)) throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);

        userRepository.deleteById(id);
    }

    private void validateAccessibleUser(Long id, Long accessorId) {
        if(!id.equals(accessorId)){
            throw new UnauthorizedException(ErrorCode.USER_INACCESSIBLE);
        }
    }
}
