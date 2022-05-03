package com.eastshine.auction.member.domain.role;

import com.eastshine.auction.member.domain.Member;
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

@EqualsAndHashCode(of = {"member", "roleType"})
@NoArgsConstructor
@Embeddable
public class RoleId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_role_member"))
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private RoleType roleType;

    public RoleId(Member member, RoleType roleType) {
        this.member = member;
        this.roleType = roleType;
    }
}
