package com.eastshine.auction.user.domain;

import com.eastshine.auction.common.model.BaseTimeEntity;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.user.domain.role.Role;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자
 */
@ToString
@EqualsAndHashCode(callSuper=false, of = "id")
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "roleId.user", cascade = CascadeType.ALL)
    private List<Role> roles = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status = User.Status.ACTIVE;

    public enum Status{
        ACTIVE,
        DROPOUT
    }

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public void addRole(Role role) {
        // 복합키(Role) 사용은 setter를 내포.
        roles.add(role);
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }

    /**
     * 비밀번호가 유효하고 활성중인 회원인지 검증한다.
     * @param password 비밀번호
     * @param passwordEncoder 비밀번호 인코더
     * @return 비밀번호가 일치하고 활성중인 회원일 경우 True, 비밀번호가 일치하지 않거나 활성중인 회원이 아닌 경우 False.
     */
    public boolean authenticate(String password, PasswordEncoder passwordEncoder) {
        return status == User.Status.ACTIVE && passwordEncoder.matches(password, this.password);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public UserInfo toUserInfo() {
        return UserInfo.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .roles(this.roles.stream()
                        .map(role -> role.getRoleId().getRole())
                        .collect(Collectors.toList()))
                .build();
    }
}