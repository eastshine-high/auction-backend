package com.eastshine.auction.common.model;

import com.eastshine.auction.user.domain.role.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@EqualsAndHashCode(of = "id")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "userInfo", timeToLive = 60*100) // 100분
public class UserInfo {

    @Id
    private Long id;
    private String email;
    private String nickname;
    private List<RoleType> roles;
}
