package com.tkahng.spring_auth.rbac;

import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import com.tkahng.spring_auth.dto.PermissionFilter;
import com.tkahng.spring_auth.dto.RoleFilter;
import com.tkahng.spring_auth.spec.PermissionSpecifications;
import com.tkahng.spring_auth.spec.RoleSpecifications;
import com.tkahng.spring_auth.users.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RbacServiceImpl implements RbacService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final Map<String, List<String>> rolePermissionMap;

    public RbacServiceImpl(
            PermissionRepository permissionRepository, RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository, UserRoleRepository userRoleRepository
    ) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionMap = new java.util.HashMap<>();
        this.rolePermissionMap.put("basic", List.of("basic"));
        this.rolePermissionMap.put("pro", List.of("basic", "pro"));
        this.rolePermissionMap.put("advanced", List.of("basic", "pro", "advanced"));
        this.rolePermissionMap.put("admin", List.of("basic", "pro", "advanced", "admin"));
    }


    @Override
    public Permission createPermission(@NonNull CreatePermissionDto createPermissionDto) {
        return permissionRepository.saveAndFlush(Permission.builder()
                .name(createPermissionDto.getName())
                .description(createPermissionDto.getDescription())
                .build());
    }

    @Override
    public Map<String, List<String>> getRolePermissionMap() {
        return rolePermissionMap;
    }

    @Override
    public Role createRole(@NonNull CreateRoleDto createRoleDto) {
        return roleRepository.saveAndFlush(Role.builder()
                .name(createRoleDto.getName())
                .description(createRoleDto.getDescription())
                .build());
    }

    @Override
    public void assignRoleToUser(@NotNull User user, @NotNull Role role) {
        var existingUserRole = userRoleRepository.findByUserIdAndRoleId(
                user.getId(),
                role.getId()
        );
        if (existingUserRole.isPresent()) {
            return;
        }
        UserRole userRole = UserRole.builder()
                .id(
                        UserRoleId.builder()
                                .userId(user.getId())
                                .roleId(role.getId())
                                .build()
                )
                .user(user)
                .role(role)
                .build();
        userRoleRepository.saveAndFlush(userRole);
    }

    @Override
    public void assignPermissionToRole(Role role, Permission permission) {
        var existingRolePermission = rolePermissionRepository.findByRoleIdAndPermissionId(
                role.getId(),
                permission.getId()
        );
        if (existingRolePermission.isPresent()) {
            return;
        }
        var rolePermission = RolePermission.builder()
                .id(
                        RolePermissionId.builder()
                                .roleId(role.getId())
                                .permissionId(permission.getId())
                                .build())
                .role(role)
                .permission(permission)
                .build();
        rolePermissionRepository.saveAndFlush(rolePermission);
    }

    @Override
    public Optional<Permission> findPermissionById(UUID id) {
        return permissionRepository.findById(id);
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Role findOrCreateRoleByName(String name) {
        var role = roleRepository.findByName(name)
                .orElse(null);
        if (role == null) {
            role = roleRepository.saveAndFlush(Role.builder()
                    .name(name)
                    .build());
        }
        return role;
    }

    @Override
    public Page<Role> findAllRoles(RoleFilter filter, Pageable pageable) {
        return roleRepository.findAll(RoleSpecifications.buildSpec(filter), pageable);
    }

    @Override
    public Optional<Role> findRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Permission> findPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    public Permission findOrCreatePermissionByName(String name) {
        var permission = permissionRepository.findByName(name)
                .orElse(null);
        if (permission == null) {
            permission = permissionRepository.saveAndFlush(Permission.builder()
                    .name(name)
                    .build());
        }
        return permission;
    }

    @Override
    public Page<Permission> findAllPermissions(PermissionFilter filter, Pageable pageable) {
        return permissionRepository.findAll(PermissionSpecifications.buildSpec(filter), pageable);
    }

    @Override
    public void initRolesAndPermissions() {
        for (Map.Entry<String, List<String>> entry : this.rolePermissionMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            var role = findOrCreateRoleByName(key);

            for (String value : values) {
                var permission = findOrCreatePermissionByName(value);
                assignPermissionToRole(role, permission);
            }
        }
    }

    @Override
    public List<String> getRoleNamesByUserId(UUID userId) {
        return findAllRoles(
                RoleFilter.builder()
                        .userId(userId)
                        .build(), Pageable.unpaged()
        )
                .stream()
                .map(Role::getName)
                .toList();
    }

    @Override
    public List<String> getPermissionNamesByUserId(UUID userId) {
        return findAllPermissions(
                PermissionFilter.builder()
                        .userId(userId)
                        .build(), Pageable.unpaged()
        )
                .stream()
                .map(Permission::getName)
                .toList();
    }
}
