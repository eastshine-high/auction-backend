package com.eastshine.auction.user.domain.role;

import com.eastshine.auction.user.domain.User;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@EqualsAndHashCode(of = {"user", "role"})
@NoArgsConstructor
@Embeddable
public class UserRoleId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_role_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_type", foreignKey = @ForeignKey(name = "fk_user_role_role"))
    private Role role;

    public UserRoleId(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public User getUser() {
        return user;
    }
}
