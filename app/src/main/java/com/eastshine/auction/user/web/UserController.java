package com.eastshine.auction.user.web;

import com.eastshine.auction.user.application.UserService;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.web.dto.UserSignupDto;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping()
    public ResponseEntity signUpMember(@RequestBody @Validated UserSignupDto request) {
        User requestSignup = userMapper.of(request);

        User signedUpUser = userService.signUpUser(requestSignup);

        return ResponseEntity.created(URI.create("/users/" + signedUpUser.getId())).build();
    }
}
