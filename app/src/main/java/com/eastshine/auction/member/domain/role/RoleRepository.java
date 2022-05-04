package com.eastshine.auction.member.domain.role;

import com.eastshine.auction.member.domain.role.Role;
import com.eastshine.auction.member.domain.role.RoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, RoleId> {
}
