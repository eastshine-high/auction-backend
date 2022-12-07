package com.eastshine.auction.user.web;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.user.application.UserService;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserMapper;
import com.eastshine.auction.user.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user-api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping()
    public ResponseEntity signUpMember(@RequestBody @Validated UserDto.Signup request) {
        User requestSignup = userMapper.of(request);

        User signedUpUser = userService.signUpUser(requestSignup);

        return ResponseEntity.created(URI.create("/user-api/v1/users/" + signedUpUser.getId())).build();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.Info getUser(@PathVariable Long id) {
        return userService.findUserInfo(id);
    }

    @PatchMapping("{id}/nickname")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public void patchNickname(
            @PathVariable Long id,
            @RequestBody @Validated UserDto.PatchNickname requestPatch,
            Authentication authentication) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        userService.updateNickname(id, requestPatch.getNickname(), userInfo.getId());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public void deleteSeller(@PathVariable Long id, Authentication authentication) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        userService.deleteUser(id, userInfo.getId());
    }
}
