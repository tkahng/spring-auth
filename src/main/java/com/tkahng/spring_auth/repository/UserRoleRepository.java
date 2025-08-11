package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.UserRole;
import com.tkahng.spring_auth.domain.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId>, JpaSpecificationExecutor<UserRole> {
    Optional<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId);
}
