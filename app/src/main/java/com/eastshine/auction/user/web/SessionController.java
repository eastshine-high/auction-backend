package com.eastshine.auction.user.web;

import com.eastshine.auction.user.application.AuthenticationService;
import com.eastshine.auction.user.web.dto.SessionRequestDto;
import com.eastshine.auction.user.web.dto.SessionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/session")
public class SessionController {
    public static final String TOKEN_TYPE_BEARER = "Bearer";

    private final AuthenticationService authenticationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponseDto login(@RequestBody SessionRequestDto sessionRequestDto) {
        String email = sessionRequestDto.getEmail();
        String password = sessionRequestDto.getPassword();

        String accessToken = authenticationService.login(email, password);

        return SessionResponseDto.builder()
                .tokenType(TOKEN_TYPE_BEARER)
                .accessToken(accessToken)
                .build();
    }
}
