package com.eastshine.auction.user.domain.role;

import com.eastshine.auction.user.domain.User;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@EqualsAndHashCode(of = {"user", "role"})
@NoArgsConstructor
@Embeddable
public class RoleId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_role_user"))
    private User user;

    @Enumerated(value = EnumType.STRING)
    private RoleType role;

    public RoleId(User user, RoleType role) {
        this.user = user;
        this.role = role;
    }

    public RoleType getRole() {
        return role;
    }

    public User getUser() {
        return user;
    }
}
