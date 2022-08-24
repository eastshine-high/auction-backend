package com.eastshine.auction.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class SellerDto {

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Signup{

        @NotBlank
        private String nickname;

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        @Pattern(regexp = "\\d{10,10}")
        private String businessNumber;
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
        private String businessNumber;
    }
}
