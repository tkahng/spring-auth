package com.tkahng.spring_auth.identity;

import com.tkahng.spring_auth.auth.AuthService;
import com.tkahng.spring_auth.auth.PasswordService;
import com.tkahng.spring_auth.auth.dto.AuthDto;
import com.tkahng.spring_auth.auth.dto.AuthProvider;
import com.tkahng.spring_auth.user.User;
import com.tkahng.spring_auth.user.UserRepository;
import com.tkahng.spring_auth.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IdentityServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private IdentityRepository identityRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordService passwordService;

    @Test
    @Rollback
    public void testThatAccountCanBeCreatedAndRecalled() {
        User user = userService.createUser(new AuthDto().setEmail("email1@email.com"));
        Identity identity = identityService.createAccount(Identity.builder()
                .user(user)
                .accountId("accountId1")
                .providerId("providerId1")
                .build());

        Optional<Identity> result = identityService.findById(identity.getId());
        assertThat(result).isPresent();
        var resultAccount = result.get();
        assertThat(resultAccount).isEqualTo(identity);
    }

    @Test
    @Rollback
    public void testThatMultipleAccountsCanBeCreatedAndRecalled() {
        User user = userService.createUser(new AuthDto().setEmail("email3@email.com"));
        Identity identityA = identityService.createAccount(Identity.builder()
                .user(user)
                .accountId("accountId2A")
                .providerId("providerId2A")
                .build());
        Identity identityB = identityService.createAccount(Identity.builder()
                .user(user)
                .accountId("accountId2B")
                .providerId("providerId2B")
                .build());

        Iterable<Identity> result = identityRepository.findAll();
        assertThat(result)
                .hasSize(2)
                .containsExactly(identityA, identityB);
    }

    @Test
    @Rollback
    public void testThatAccountsWithSameProviderIdCannotBeCreated() {
        User user = userService.createUser(new AuthDto().setEmail("email4@email.com"));
        Identity identityA = identityService.createAccount(Identity.builder()
                .user(user)
                .accountId("accountId3A")
                .providerId("providerId3A")
                .build());

        //
        try {
            Identity identityB = identityService.createAccount(Identity.builder()
                    .user(user)
                    .accountId("accountId3B")
                    .providerId("providerId3B")
                    .build());
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("duplicate key value violates unique constraint");
        }
    }

    @Test
    @Rollback
    public void testThatAccountCanBeDeleted() {
        User user = userService.createUser(new AuthDto().setEmail("email5@email.com"));

        Identity identity = identityService.createAccount(Identity.builder()
                .user(user)
                .accountId("accountId4")
                .providerId("providerId4")
                .build());

        identityService.deleteAccount(identity);
        Optional<Identity> result = identityService.findById(identity.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @Rollback
    public void testThatAccountPasswordCanBeUpdated() {
        var user = authService.createUserAndAccount(AuthDto.builder()
                .email("test@example.com")
                .name("test")
                .password("Password123!")
                .accountId("test")
                .provider(AuthProvider.CREDENTIALS)
                .build());
        var updatedPassword = "updatedPassword";
        var updatedHashedPassword = passwordService.encode(updatedPassword);
        identityService.updatePasswordById(
                user.getIdentity()
                        .getId(), updatedHashedPassword
        );
        var account = identityRepository.findByUserIdAndProviderId(
                        user.getUser()
                                .getId(), AuthProvider.CREDENTIALS.toString()
                )
                .orElseThrow();
        assertThat(account.getPasswordHash()).isEqualTo(updatedHashedPassword);
    }

    @Test
    @Rollback
    public void testThatAccountRefreshTokenCanBeUpdated() {
        var user = authService.createUserAndAccount(AuthDto.builder()
                .email("test@example.com")
                .name("test")
                .refreshToken("refreshToken")
                .accountId("test")
                .provider(AuthProvider.GOOGLE)
                .build());
        var updatedRefreshToken = "updatedRefreshToken";
        identityService.updateRefreshTokenById(
                user.getIdentity()
                        .getId(), updatedRefreshToken
        );
        var account = identityRepository.findByUserIdAndProviderId(
                        user.getUser()
                                .getId(), AuthProvider.GOOGLE.toString()
                )
                .orElseThrow();
        assertThat(account.getRefreshToken()).isEqualTo(updatedRefreshToken);
    }

    @Test
    @Rollback
    public void testThatAccountUpdatedAtCanBeUpdated() throws InterruptedException {
        var user = authService.createUserAndAccount(AuthDto.builder()
                .email("test@example.com")
                .name("test")
                .refreshToken("refreshToken")
                .accountId("test")
                .provider(AuthProvider.GOOGLE)
                .build());
        var initialUpdatedAt = user.getIdentity()
                .getUpdatedAt();

        TimeUnit.SECONDS.sleep(1);
        var updatedTime = LocalDateTime.now();
        identityService.updateUpdatedAtById(
                user.getIdentity()
                        .getId(), updatedTime
        );
        var account = identityRepository.findByUserIdAndProviderId(
                        user.getUser()
                                .getId(), AuthProvider.GOOGLE.toString()
                )
                .orElseThrow();
        var accountUpdatedAt = account.getUpdatedAt();

        assertThat(accountUpdatedAt
        ).isAfter(initialUpdatedAt)
                .isAfterOrEqualTo(updatedTime);
    }
}