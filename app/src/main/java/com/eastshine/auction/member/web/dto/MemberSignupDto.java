package com.eastshine.auction.member.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupDto {

    @NotBlank
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}


