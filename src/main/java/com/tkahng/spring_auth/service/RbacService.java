package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Permission;
import com.tkahng.spring_auth.domain.Role;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import com.tkahng.spring_auth.dto.RoleFilter;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface RbacService {
    Role createRole(@Valid CreateRoleDto createRoleDto);

    Optional<Role> findRoleByName(String name);

    Optional<Role> findRoleById(UUID id);

    Role findOrCreateRoleByName(String name);

    Page<Role> findAllRoles(RoleFilter filter, Pageable pageable);

    void assignRoleToUser(User user, Role role);

    Permission createPermission(@NonNull CreatePermissionDto createPermissionDto);

    Optional<Permission> findPermissionById(UUID id);

    Optional<Permission> findPermissionByName(String name);

    Permission findOrCreatePermissionByName(String name);

    void assignPermissionToRole(Role role, Permission permission);

    void initRolesAndPermissions();
}
