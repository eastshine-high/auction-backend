package com.eastshine.auction.user.domain.role;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@EqualsAndHashCode(of = {"roleType"})
@NoArgsConstructor
@Entity
public class Role {

    @Id @Enumerated(value = EnumType.STRING)
    private RoleType roleType;

    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    public RoleType getRoleType() {
        return roleType;
    }
}
