package com.eastshine.auction.user.application;

import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.role.RoleType;
import com.eastshine.auction.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;

    @Transactional
    public void addAdminRole(User user) {
        user.addRole(RoleType.ADMIN);
        userRepository.save(user);
    }
}
