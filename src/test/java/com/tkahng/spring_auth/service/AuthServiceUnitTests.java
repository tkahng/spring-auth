package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.*;
import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AuthServiceUnitTests {

    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordService passwordService;
    @MockitoBean
    private AccountRepository accountRepository;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private RbacService rbacService;
    @MockitoBean
    private TokenService tokenService;
    //@MockitoBean
    //private JavaMailSender mailSender;

    //@Autowired
    //private MailServiceImpl mailService;

    @Test
    public void testLoginSuccess() {
        var userId = UUID.randomUUID();
        var user = User.builder()
                .id(userId)
                .email("test@example.com")
                .build();
        var password = "Password123!";
        var hashedPassword = passwordService.encode(password);
        var account = Account.builder()
                .user(user)
                .providerId(AuthProvider.CREDENTIALS.toString())
                .accountId("test@example.com")
                .password_hash(hashedPassword)
                .build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(accountRepository.findByUserIdAndProviderId(
                user.getId(),
                AuthProvider.CREDENTIALS.toString()
        )).thenReturn(Optional.of(account));
        when(rbacService.findAllRoles(
                RoleFilter.builder()
                        .userId(userId)
                        .build(), Pageable.unpaged()
        )).thenReturn(Page.empty());
        when(rbacService.findAllPermissions(
                PermissionFilter.builder()
                        .userId(userId)
                        .build(), Pageable.unpaged()
        )).thenReturn(Page.empty());
        when(tokenService.generateRefreshToken("test@example.com")).thenReturn("refreshToken");
        var dto = AuthDto.builder()
                .email("test@example.com")
                .password(password)
                .provider(AuthProvider.CREDENTIALS)
                .accountId("test@example.com")
                .build();
        AuthenticationResponse result = null;
        try {
            result = authService.login(dto);
        } catch (Exception e) {

        }
        assertThat(result).isNotNull();
    }

    @Test
    public void testLoginFailUserNotFound() {
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        var dto = AuthDto.builder()
                .email("email")
                .password("password")
                .provider(AuthProvider.CREDENTIALS)
                .accountId("email")
                .build();
        AuthenticationResponse result = null;
        try {
            result = authService.login(dto);
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .contains("user not found");
        }
    }

    @Test
    public void testLoginFailUserAccountNotFound() {
        var user = User.builder()
                .email("email")
                .build();

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(User.builder()
                .email("email")
                .build()));
        when(accountRepository.findByUserIdAndProviderId(
                user.getId(),
                AuthProvider.CREDENTIALS.toString()
        )).thenReturn(Optional.empty());
        var dto = AuthDto.builder()
                .email("email")
                .password("password")
                .provider(AuthProvider.CREDENTIALS)
                .accountId("email")
                .build();
        AuthenticationResponse result = null;
        try {
            result = authService.login(dto);
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .contains("user account not found");
        }
    }

    @Test
    public void testLoginFailUserAccountPasswordNull() {
        var user = User.builder()
                .email("email")
                .build();
        var account = Account.builder()
                .user(user)
                .providerId(AuthProvider.CREDENTIALS.toString())
                .accountId("email")
                .build();
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(accountRepository.findByUserIdAndProviderId(
                user.getId(),
                AuthProvider.CREDENTIALS.toString()
        )).thenReturn(Optional.of(account));
        var dto = AuthDto.builder()
                .email("email")
                .password("password")
                .provider(AuthProvider.CREDENTIALS)
                .accountId("email")
                .build();
        AuthenticationResponse result = null;
        try {
            result = authService.login(dto);
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .contains("password not found");
        }
    }

    @Test
    public void testLoginFailUserAccountPasswordNotMatch() {
        var user = User.builder()
                .email("email")
                .build();
        var wrongPassword = "wrongPassword";
        var wrongHashedPassword = passwordService.encode(wrongPassword);
        var account = Account.builder()
                .user(user)
                .providerId(AuthProvider.CREDENTIALS.toString())
                .password_hash(wrongHashedPassword)
                .accountId("email")
                .build();
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(accountRepository.findByUserIdAndProviderId(
                user.getId(),
                AuthProvider.CREDENTIALS.toString()
        )).thenReturn(Optional.of(account));
        var dto = AuthDto.builder()
                .email("email")
                .password("password")
                .provider(AuthProvider.CREDENTIALS)
                .accountId("email")
                .build();
        AuthenticationResponse result = null;
        try {
            result = authService.login(dto);
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .contains("invalid password");
        }
    }


}
