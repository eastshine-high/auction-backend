package com.eastshine.auction.user.domain.role;

import com.eastshine.auction.common.model.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * 권한
 */
@EqualsAndHashCode(callSuper=false, of = {"userRoleId"})
@NoArgsConstructor
@Entity
public class UserRole extends BaseEntity {

    @EmbeddedId
    private UserRoleId userRoleId;

    public UserRole(UserRoleId userRoleId) {
        this.userRoleId = userRoleId;
    }

    public RoleType getRoleType() {
        return userRoleId.getRole().getRoleType();
    }
}
