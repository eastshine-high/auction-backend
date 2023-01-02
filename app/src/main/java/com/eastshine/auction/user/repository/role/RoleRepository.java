package com.eastshine.auction.user.repository.role;

import com.eastshine.auction.user.domain.role.UserRole;
import com.eastshine.auction.user.domain.role.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
