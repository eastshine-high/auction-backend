package com.eastshine.auction.member.web;

import com.eastshine.auction.member.application.MemberService;
import com.eastshine.auction.member.domain.Member;
import com.eastshine.auction.member.web.dto.MemberSignupDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @PostMapping()
    public ResponseEntity signUpMember(@RequestBody @Validated MemberSignupDto request) {
        Member requestSignup = memberMapper.of(request);

        Member signedUpMember = memberService.signUpMember(requestSignup);

        return ResponseEntity.created(URI.create("/members/" + signedUpMember.getId())).build();
    }
}
