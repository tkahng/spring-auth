package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Permission;
import com.tkahng.spring_auth.domain.Role;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import jakarta.validation.Valid;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface RbacService {
    Role createRole(@Valid CreateRoleDto createRoleDto);

    Optional<Role> findRoleByName(String name);

    Role findOrCreateRoleByName(String name);

    Optional<Role> findRoleById(UUID id);

    void assignRoleToUser(User user, Role role);

    Permission createPermission(@NonNull CreatePermissionDto createPermissionDto);

    Optional<Permission> findPermissionById(UUID id);

    Optional<Permission> findPermissionByName(String name);

    Permission findOrCreatePermissionByName(String name);

    void assignPermissionToRole(Role role, Permission permission);

    void initRolesAndPermissions();
}
