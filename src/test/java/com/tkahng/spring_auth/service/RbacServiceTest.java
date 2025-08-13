package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import com.tkahng.spring_auth.dto.PermissionFilter;
import com.tkahng.spring_auth.dto.RoleFilter;
import com.tkahng.spring_auth.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RbacServiceTest {
    @Autowired
    private RbacService rbacService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

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
    void testCreatePermissionNoName() {
        var dto = CreatePermissionDto.builder()
                .description("test")
                .build();
        Assertions.assertThrows(Exception.class, () -> {
            rbacService.createPermission(dto);
        });
    }

    @Test
    @Rollback
    void createRole() {
        // create
        var dto = CreateRoleDto.builder()
                .name("test")
                .description("test")
                .build();
        var result = rbacService.createRole(dto);
        assertThat(result).isNotNull();
        // find by name
        var findResult = rbacService.findRoleByName("test")
                .orElseThrow();
        assertThat(findResult).isNotNull()
                .isEqualTo(result);
        // find by id
        var findResult2 = rbacService.findRoleById(result.getId())
                .orElseThrow();
        assertThat(findResult2).isNotNull()
                .isEqualTo(result);
        // create without description
        var dtoWithoutDescription = CreateRoleDto.builder()
                .name("test2")
                .build();
        var result2 = rbacService.createRole(dtoWithoutDescription);
        assertThat(result2).isNotNull();
        assertThat(result2.getName()).isEqualTo("test2");
        assertThat(result2.getDescription()).isEqualTo(null);
    }

    @Test
    @Rollback
    void testCreateRoleNoName() {
        var dto = CreateRoleDto.builder()
                .description("test")
                .build();

        Assertions.assertThrows(Exception.class, () -> {
            rbacService.createRole(dto);
        });
    }

    @Test
    @Rollback
    void assignRoleToUser() {
        var user = userRepository.saveAndFlush(User.builder()
                .name("test")
                .email("test")
                .build());
        var role = rbacService.createRole(CreateRoleDto.builder()
                .name("test")
                .description("test")
                .build());
        rbacService.assignRoleToUser(user, role);
        var userRole = userRoleRepository.findByUserIdAndRoleId(user.getId(), role.getId())
                .orElseThrow();
        assertThat(userRole).isNotNull();
        assertThat(user.getId()).isEqualTo(userRole.getId()
                .getUserId());
        assertThat(role.getId()).isEqualTo(userRole.getId()
                .getRoleId());
    }

    @Test
    void assignPermissionToRole() {
        var role = rbacService.findOrCreateRoleByName("test");
        var permission = rbacService.findOrCreatePermissionByName("test");
        rbacService.assignPermissionToRole(role, permission);
        var userRole = rolePermissionRepository.findByRoleIdAndPermissionId(role.getId(), permission.getId())
                .orElseThrow();
        assertThat(userRole).isNotNull();
        assertThat(role.getId()).isEqualTo(userRole.getId()
                .getRoleId());
        assertThat(permission.getId()).isEqualTo(userRole.getId()
                .getPermissionId());
    }

    @Test
    void findPermissionById() {
        var permission = rbacService.findOrCreatePermissionByName("test");
        var result = rbacService.findPermissionById(permission.getId())
                .orElseThrow();
        assertThat(result).isNotNull()
                .matches(p -> p.getId()
                        .equals(permission.getId()));
    }

    @Test
    void findRoleByName() {
        var role = rbacService.findOrCreateRoleByName("test");
        var result = rbacService.findRoleByName(role.getName())
                .orElseThrow();
        assertThat(result).isNotNull()
                .matches(p -> p.getName()
                        .equals(role.getName()));
    }

    @Test
    void findRoleById() {
        var role = rbacService.findOrCreateRoleByName("test");
        var result = rbacService.findRoleById(role.getId())
                .orElseThrow();
        assertThat(result).isNotNull()
                .matches(p -> p.getId()
                        .equals(role.getId()));
    }

    @Test
    void findPermissionByName() {
        var permission = rbacService.findOrCreatePermissionByName("test");
        var result = rbacService.findPermissionByName(permission.getName())
                .orElseThrow();
        assertThat(result).isNotNull()
                .matches(p -> p.getName()
                        .equals(permission.getName()));
    }

    @Test
    void notAssignedToRole() {
    }

    @Test
    void findOrCreateRoleByName() {
        var role = rbacService.findOrCreateRoleByName("test");
        var result = rbacService.findRoleByName(role.getName())
                .orElseThrow();
        assertThat(result).isNotNull()
                .matches(p -> p.getName()
                        .equals(role.getName()));
    }

    @Test
    void findAllRoles() {
    }

    @Test
    void findOrCreatePermissionByName() {
        var permission = rbacService.findOrCreatePermissionByName("test");
        var result = rbacService.findPermissionByName(permission.getName())
                .orElseThrow();
        assertThat(result).isNotNull()
                .matches(p -> p.getName()
                        .equals(permission.getName()));
    }

    @Test
    void initRolesAndPermissions() {
        rbacService.initRolesAndPermissions();
        var roles = rbacService.findAllRoles(RoleFilter.builder()
                .build(), Pageable.unpaged());
        assertThat(roles).isNotNull();
        assertThat(roles.getTotalElements()).isEqualTo(4);
        var permissions = rbacService.findAllPermissions(PermissionFilter.builder()
                .build(), Pageable.unpaged());
        assertThat(permissions).isNotNull();
        assertThat(permissions.getTotalElements()).isEqualTo(4);

        var nameMap = rbacService.getRolePermissionMap();
        for (var entry : nameMap.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var role = rbacService.findRoleByName(key)
                    .orElseThrow();
            log.info("role {}", role.getName());
            assertThat(role).isNotNull();
            var rolePermissions = rbacService.findAllPermissions(PermissionFilter.builder()
                    .roleId(role.getId())
                    .build(), Pageable.unpaged());
            for (var permission : rolePermissions.get()
                    .toList()) {
                log.info("role {} permission {}", role.getName(), permission.getName());
                assertThat(permission.getName()).isIn(value);
            }
            var permissionsCount = rolePermissions.getNumberOfElements();
            assertThat(permissionsCount).isEqualTo(value.size());
        }
    }
}