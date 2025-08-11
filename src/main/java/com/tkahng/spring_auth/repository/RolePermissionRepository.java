package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.RolePermission;
import com.tkahng.spring_auth.domain.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
}
