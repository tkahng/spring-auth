package com.tkahng.spring_auth.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    Optional<RolePermission> findByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
}
