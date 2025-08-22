package com.tkahng.spring_auth.controller;


import com.tkahng.spring_auth.annotation.Authenticated;
import com.tkahng.spring_auth.annotation.CurrentUser;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.*;
import com.tkahng.spring_auth.service.AuthService;
import com.tkahng.spring_auth.service.MailService;
import com.tkahng.spring_auth.service.TokenService;
import com.tkahng.spring_auth.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final MailService mailService;
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("/signup")
    public AuthenticationResponse signup(@RequestBody @NotNull RegisterRequest request) {
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
    public AuthenticationResponse login(@RequestBody @NotNull LoginRequest request) {
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
    public AuthenticationResponse refreshToken(@RequestBody @NotNull RefreshTokenRequest request) {
        var identifier = tokenService.validateRefreshToken(request.getRefreshToken());
        var user = userService.findUserByEmail(identifier)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        return authService.generateToken(user);
    }

    @PostMapping("/request-verification")
    @Authenticated
    public void requestVerification(@CurrentUser @NotNull User user) {
        if (user.getEmailVerifiedAt() != null) {
            throw new IllegalStateException("email already verified");
        }
        mailService.sendVerificationMail(user);
    }

    @Authenticated
    @PostMapping("/confirm-verification/{token}")
    public void confirmVerificationPost(@CurrentUser @NotNull User currentUser, @PathVariable String token) {
        var identifier = tokenService.validateEmailVerificationToken(token);
        var user = userService.findUserByEmail(identifier)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        if (user.getId() != currentUser.getId()) {
            throw new IllegalStateException("current user does not match user of this token");
        }
        userService.updateUserEmailVerifiedAt(user.getId(), OffsetDateTime.now());
    }

    @PostMapping("/set-password")
    public void setPassword(@CurrentUser User user, @RequestBody @NotNull SetPasswordRequest request) {
        authService.setPassword(user, request);
    }

    @PostMapping("/request-password-reset")
    public void requestPasswordReset(@RequestBody @NotNull RequestPasswordResetRequest request) {
        //authService.requestPasswordReset(request.getEmail());
    }
}
