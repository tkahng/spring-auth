package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.RolePermission;
import com.tkahng.spring_auth.domain.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    Optional<RolePermission> findByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
}
