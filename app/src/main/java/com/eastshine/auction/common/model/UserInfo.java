package com.eastshine.auction.common.model;

import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.role.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long id;
    private String email;
    private String nickname;
    private List<RoleType> roles;

    public UserInfo(User user) {
        id = user.getId();
        email = user.getEmail();
        nickname = user.getNickname();
        roles = user.getRoles().stream()
                .map(role -> role.getRoleId().getRole())
                .collect(Collectors.toList());
    }
}
