package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import com.tkahng.spring_auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RbacServiceTest {
    @Autowired
    private RbacService rbacService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Rollback
    void createPermission() {
        var dto = CreatePermissionDto.builder()
                .name("test")
                .description("test")
                .build();
        var result = rbacService.createPermission(dto);
        assertThat(result).isNotNull();
        var findResult = rbacService.findPermissionByName("test")
                .orElseThrow();
        assertThat(findResult).isNotNull()
                .isEqualTo(result);
        var findResult2 = rbacService.findPermissionById(result.getId())
                .orElseThrow();
        assertThat(findResult2).isNotNull()
                .isEqualTo(result);
    }

    @Test
    @Rollback
    void createRole() {
        var dto = CreateRoleDto.builder()
                .name("test")
                .description("test")
                .build();
        var result = rbacService.createRole(dto);
        assertThat(result).isNotNull();
        var findResult = rbacService.findRoleByName("test")
                .orElseThrow();
        assertThat(findResult).isNotNull()
                .isEqualTo(result);
        var findResult2 = rbacService.findRoleById(result.getId())
                .orElseThrow();
        assertThat(findResult2).isNotNull()
                .isEqualTo(result);
    }

    @Test
    void assignRoleToUser() {
        var user = userRepository.save(User.builder()
                .name("test")
                .email("test")
                .build());
        var role = rbacService.createRole(CreateRoleDto.builder()
                .name("test")
                .description("test")
                .build());
        rbacService.assignRoleToUser(user, role);

    }

    @Test
    void assignPermissionToRole() {
    }

    @Test
    void findPermissionById() {
    }

    @Test
    void findRoleByName() {
    }

    @Test
    void findRoleById() {
    }

    @Test
    void findPermissionByName() {
    }
}