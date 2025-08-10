package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
