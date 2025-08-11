package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.RolePermission;
import com.tkahng.spring_auth.domain.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
}
