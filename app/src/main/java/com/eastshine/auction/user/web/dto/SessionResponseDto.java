package com.eastshine.auction.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SessionResponseDto {
    private String tokenType;
    private String accessToken;
}
