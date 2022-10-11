package com.eastshine.auction.user.domain.role;

import com.eastshine.auction.common.model.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * 권한
 */
@EqualsAndHashCode(callSuper=false, of = {"roleId"})
@NoArgsConstructor
@Entity
public class Role extends BaseEntity {

    @EmbeddedId
    private RoleId roleId;

    public Role(RoleId roleId) {
        this.roleId = roleId;
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public void setSelfCreation() {
        super.createdBy = roleId.getUser().getId();
        super.lastModifiedBy = roleId.getUser().getId();
    }
}
