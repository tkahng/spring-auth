package com.tkahng.spring_auth.controller;


import com.tkahng.spring_auth.dto.*;
import com.tkahng.spring_auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public AuthenticationResponse signup(@RequestBody RegisterRequest request) throws Exception {
        var authDto = AuthDto.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .provider(AuthProvider.CREDENTIALS)
                .accountId(request.getEmail())
                .build();
        return authService.signup(authDto);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest request) throws Exception {
        var authDto = AuthDto.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .provider(AuthProvider.CREDENTIALS)
                .accountId(request.getEmail())
                .build();
        return authService.login(authDto);
    }
}
