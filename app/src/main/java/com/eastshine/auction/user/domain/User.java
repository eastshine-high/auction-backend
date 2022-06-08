package com.eastshine.auction.user.domain;

import com.eastshine.auction.common.model.BaseTimeEntity;
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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 사용자 도메인
 */
@ToString
@EqualsAndHashCode(callSuper=false, of = "id")
@Getter
@NoArgsConstructor
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
    private Status status;

    public enum Status{
        ACTIVE,
        DROPOUT
    }

    @PrePersist
    public void prePersist() {
        if(Objects.isNull(this.status)){
            this.status = User.Status.ACTIVE;
        }
    }

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
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
}
