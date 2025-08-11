package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.*;
import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import com.tkahng.spring_auth.repository.PermissionRepository;
import com.tkahng.spring_auth.repository.RolePermissionRepository;
import com.tkahng.spring_auth.repository.RoleRepository;
import com.tkahng.spring_auth.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RbacServiceImpl implements RbacService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public Permission createPermission(@NonNull CreatePermissionDto createPermissionDto) {
        return permissionRepository.save(Permission.builder()
                .name(createPermissionDto.getName())
                .description(createPermissionDto.getDescription())
                .build());
    }

    @Override
    public Role createRole(@NonNull CreateRoleDto createRoleDto) {
        return roleRepository.save(Role.builder()
                .name(createRoleDto.getName())
                .description(createRoleDto.getDescription())
                .build());
    }

    @Override
    public void assignRoleToUser(User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    @Override
    public void assignPermissionToRole(Role role, Permission permission) {
        var rolePermission = RolePermission.builder()
                .role(role)
                .permission(permission)
                .build();
        rolePermissionRepository.save(rolePermission);
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
    public Optional<Role> findRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Permission> findPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }

}
