package com.eastshine.auction.member.domain;

import com.eastshine.auction.common.model.BaseTimeEntity;
import com.eastshine.auction.member.domain.role.Role;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 회원 도메인
 */
@EqualsAndHashCode(callSuper=false, of = "id")
@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "roleId.member", cascade = CascadeType.ALL)
    private List<Role> roles = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    public enum Status{
        SINGUP,
        DROPOUT
    }

    @PrePersist
    public void prePersist() {
        if(Objects.isNull(this.status)){
            this.status = Member.Status.SINGUP;
        }
    }

    @Builder
    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
