package com.tkahng.spring_auth.auth;

import com.tkahng.spring_auth.auth.dto.AuthDto;
import com.tkahng.spring_auth.auth.dto.AuthProvider;
import com.tkahng.spring_auth.auth.dto.AuthenticationResponse;
import com.tkahng.spring_auth.identity.Identity;
import com.tkahng.spring_auth.identity.IdentityRepository;
import com.tkahng.spring_auth.rbac.PermissionFilter;
import com.tkahng.spring_auth.rbac.RbacService;
import com.tkahng.spring_auth.rbac.RoleFilter;
import com.tkahng.spring_auth.token.TokenService;
import com.tkahng.spring_auth.user.User;
import com.tkahng.spring_auth.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthServiceUnitTests {

    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordService passwordService;
    @MockitoBean
    private IdentityRepository identityRepository;
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
    public void testCredentialsLoginSuccess() {
        var userId = UUID.randomUUID();
        var user = User.builder()
                .id(userId)
                .email("test@example.com")
                .build();
        var password = "Password123!";
        var hashedPassword = passwordService.encode(password);
        var account = Identity.builder()
                .user(user)
                .providerId(AuthProvider.CREDENTIALS.toString())
                .accountId("test@example.com")
                .passwordHash(hashedPassword)
                .build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(identityRepository.findByUserIdAndProviderId(
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
            result = authService.credentialsLogin(dto);
        } catch (Exception e) {

        }
        assertThat(result).isNotNull();
    }

    @Test
    public void testCredentialsLoginFailUserNotFound() {
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        var dto = AuthDto.builder()
                .email("email")
                .password("password")
                .provider(AuthProvider.CREDENTIALS)
                .accountId("email")
                .build();
        AuthenticationResponse result = null;
        try {
            result = authService.credentialsLogin(dto);
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .contains("user not found");
        }
    }

    @Test
    public void testCredentialsLoginFailUserAccountNotFound() {
        var user = User.builder()
                .email("email")
                .build();

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(User.builder()
                .email("email")
                .build()));
        when(identityRepository.findByUserIdAndProviderId(
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
            result = authService.credentialsLogin(dto);
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .contains("user account not found");
        }
    }

    @Test
    public void testCredentialsLoginFailUserAccountPasswordNull() {
        var user = User.builder()
                .email("email")
                .build();
        var account = Identity.builder()
                .user(user)
                .providerId(AuthProvider.CREDENTIALS.toString())
                .accountId("email")
                .build();
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(identityRepository.findByUserIdAndProviderId(
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
            result = authService.credentialsLogin(dto);
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .contains("password not found");
        }
    }

    @Test
    public void testCredentialsLoginFailUserAccountPasswordNotMatch() {
        var user = User.builder()
                .email("email")
                .build();
        var wrongPassword = "wrongPassword";
        var wrongHashedPassword = passwordService.encode(wrongPassword);
        var account = Identity.builder()
                .user(user)
                .providerId(AuthProvider.CREDENTIALS.toString())
                .passwordHash(wrongHashedPassword)
                .accountId("email")
                .build();
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(identityRepository.findByUserIdAndProviderId(
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
            result = authService.credentialsLogin(dto);
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .contains("invalid password");
        }
    }


}
