package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Permission;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.dto.AuthProvider;
import com.tkahng.spring_auth.dto.PermissionFilter;
import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthServiceIntegrationTests {
    @Autowired
    private Flyway flyway;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private RbacService rbacService;
    @Autowired
    private UserRepository userRepository;


    @Test
    @Rollback
    public void testAuthServiceSignup() throws Exception {
        var dto = AuthDto.builder()
                .name("name")
                .email("email")
                .provider(AuthProvider.CREDENTIALS)
                .accountId("email")
                .build();
        var res = authService.credentialsSignup(dto);
        assertThat(res).isNotNull();
        var user = userService.findUserByEmail("email")
                .orElseThrow();
        assertThat(user).isNotNull()
                .matches(user1 -> user.getEmail()
                        .equals("email"));

    }

    @Test
    @Rollback
    public void testCreateSuperUser() throws Exception {
        rbacService.initRolesAndPermissions();
        authService.createSuperUser("email", "password");
        var user = userService.findUserByEmail("email")
                .orElseThrow();
        assertThat(user).isNotNull()
                .matches(user1 -> user.getEmail()
                        .equals("email"));
        var permission = rbacService.findAllPermissions(
                        PermissionFilter.builder()
                                .userId(user.getId())
                                .build(),
                        Pageable.unpaged()
                )
                .stream()
                .map(Permission::getName)
                .toList();
        assertThat(permission).contains("admin");
    }

}