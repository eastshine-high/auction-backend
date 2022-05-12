package com.eastshine.auction.common.model;

import com.eastshine.auction.user.domain.role.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
}
