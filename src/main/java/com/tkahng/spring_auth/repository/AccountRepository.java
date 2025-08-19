package com.tkahng.spring_auth.repository;


import com.tkahng.spring_auth.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUserIdAndProviderId(UUID userId, String providerId);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.passwordHash = :passwordHash, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updatePasswordById(UUID id, String passwordHash);
}
