package com.tkahng.spring_auth.rbac;

import com.tkahng.spring_auth.users.User;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RbacService {
    Map<String, List<String>> getRolePermissionMap();

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

    Page<Permission> findAllPermissions(PermissionFilter filter, Pageable pageable);

    void assignPermissionToRole(Role role, Permission permission);

    void initRolesAndPermissions();

    List<String> getRoleNamesByUserId(UUID userId);

    List<String> getPermissionNamesByUserId(UUID userId);
}
