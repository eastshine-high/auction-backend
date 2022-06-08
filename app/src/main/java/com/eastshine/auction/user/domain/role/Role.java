package com.eastshine.auction.user.domain.role;

import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * 권한
 */
@NoArgsConstructor
@Entity
public class Role {
    @EmbeddedId
    private RoleId roleId;

    public Role(RoleId roleId) {
        this.roleId = roleId;
    }

    public RoleId getRoleId() {
        return roleId;
    }
}
