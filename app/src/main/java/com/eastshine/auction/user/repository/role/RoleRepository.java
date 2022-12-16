package com.eastshine.auction.user.repository.role;

import com.eastshine.auction.user.domain.role.Role;
import com.eastshine.auction.user.domain.role.RoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, RoleId> {
}
