package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Permission;
import com.tkahng.spring_auth.domain.Role;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface RbacService {
    Permission createPermission(@NonNull CreatePermissionDto createPermissionDto);

    Role createRole(@NonNull CreateRoleDto createRoleDto);

    void assignRoleToUser(User user, Role role);

    void assignPermissionToRole(Role role, Permission permission);

    Optional<Permission> findPermissionById(UUID id);

    Optional<Role> findRoleByName(String name);

    Optional<Role> findRoleById(UUID id);

    Optional<Permission> findPermissionByName(String name);
}
