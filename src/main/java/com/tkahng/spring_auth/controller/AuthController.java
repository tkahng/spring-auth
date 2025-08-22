package com.tkahng.spring_auth.controller;


import com.tkahng.spring_auth.annotation.Authenticated;
import com.tkahng.spring_auth.annotation.CurrentUser;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.*;
import com.tkahng.spring_auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public AuthenticationResponse signup(@RequestBody @NotNull RegisterRequest request) throws Exception {
        var authDto = AuthDto.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .provider(AuthProvider.CREDENTIALS)
                .accountId(request.getEmail())
                .build();
        return authService.credentialsSignup(authDto);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody @NotNull LoginRequest request) throws Exception {
        var authDto = AuthDto.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .provider(AuthProvider.CREDENTIALS)
                .accountId(request.getEmail())
                .build();
        return authService.credentialsLogin(authDto);
    }

    @GetMapping("/me")
    @Authenticated
    public UserDto me(@CurrentUser User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .build();
    }

    @PostMapping("/refresh-token")
    public AuthenticationResponse refreshToken(@RequestBody @NotNull RefreshTokenRequest request) throws Exception {
        return authService.handleRefreshToken(request.getRefreshToken());
    }

    @PostMapping("/confirm-verification/{token}")
    public void confirmVerificationPost(@PathVariable String token) throws Exception {
        authService.handleEmailVerification(token);
    }

    @GetMapping("/confirm-verification/{token}")
    public void confirmVerificationGet(@PathVariable String token) throws Exception {
        authService.handleEmailVerification(token);
    }

    @PostMapping("/set-password")
    public void setPassword(@CurrentUser User user, @RequestBody @NotNull SetPasswordRequest request) {
        authService.setPassword(user, request);
    }
}
