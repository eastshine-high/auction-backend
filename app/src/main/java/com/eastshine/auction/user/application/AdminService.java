package com.eastshine.auction.user.application;

import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.repository.UserRepository;
import com.eastshine.auction.user.domain.role.Role;
import com.eastshine.auction.user.domain.role.RoleId;
import com.eastshine.auction.user.domain.role.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;

    @Transactional
    public void addAdminRole(User user) {
        user.addRole(new Role(new RoleId(user, RoleType.ADMIN)));
        userRepository.save(user);
    }
}
