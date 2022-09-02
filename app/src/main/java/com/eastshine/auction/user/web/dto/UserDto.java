package com.eastshine.auction.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserDto {

    @ToString
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Signup {

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String nickname;

        @NotBlank
        private String password;
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchNickname{

        @NotBlank
        String nickname;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info{
        private Long id;
        private String email;
        private String nickname;
    }
}
