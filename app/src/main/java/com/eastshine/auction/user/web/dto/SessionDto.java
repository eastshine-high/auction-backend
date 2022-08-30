package com.eastshine.auction.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SessionDto {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @Email
        @NotBlank
        String email;

        @NotBlank
        String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private String tokenType;
        private String accessToken;
    }
}
