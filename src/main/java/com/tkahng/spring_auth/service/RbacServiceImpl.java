package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.*;
import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import com.tkahng.spring_auth.repository.PermissionRepository;
import com.tkahng.spring_auth.repository.RolePermissionRepository;
import com.tkahng.spring_auth.repository.RoleRepository;
import com.tkahng.spring_auth.repository.UserRoleRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RbacServiceImpl implements RbacService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final Map<String, List<String>> rolePermissionMap;
    private final List<String> rolePermissionNames = List.of("basic", "pro", "advanced", "admin");

    public RbacServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, UserRoleRepository userRoleRepository) {
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
    public Role createRole(@NonNull CreateRoleDto createRoleDto) {
        return roleRepository.saveAndFlush(Role.builder()
                .name(createRoleDto.getName())
                .description(createRoleDto.getDescription())
                .build());
    }

    @Override
    public void assignRoleToUser(User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.saveAndFlush(userRole);
    }

    @Override
    public void assignPermissionToRole(Role role, Permission permission) {
        var rolePermission = RolePermission.builder()
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
            role = roleRepository.save(Role.builder()
                    .name(name)
                    .build());
        }
        return role;
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
        return null;
    }

    @Override
    public void initRolesAndPermissions() {
        for (Map.Entry<String, List<String>> entry : this.rolePermissionMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            System.out.println("Key: " + key);
            for (String value : values) {
                System.out.println("  Value: " + value);
            }
        }
    }


}
