package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.dto.AuthProvider;
import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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

    @Test
    public void testLoginSuccess() throws Exception {
        var user = User.builder()
                .email("email")
                .build();
        var password = "password";
        var hashedPassword = passwordService.encode(password);
        var account = Account.builder()
                .user(user)
                .providerId(AuthProvider.CREDENTIALS.toString())
                .accountId("email")
                .password_hash(hashedPassword)
                .build();
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(User.builder()
                .email("email")
                .build()));
        when(accountRepository.findByUserIdAndProviderId(user.getId(),
                AuthProvider.CREDENTIALS.toString())).thenReturn(Optional.of(account));
        var dto = AuthDto.builder()
                .email("email")
                .password(password)
                .provider(AuthProvider.CREDENTIALS)
                .accountId("email")
                .build();
        var result = authService.login(dto);
        assertThat(result).isNotNull();

    }
}
