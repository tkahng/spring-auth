package com.tkahng.spring_auth.repository;


import com.tkahng.spring_auth.domain.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUserIdAndProviderId(UUID userId, String providerId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Account a SET a.passwordHash = :passwordHash WHERE a.id = :id")
    int updatePasswordById(@Param("id") UUID id, @Param("passwordHash") String passwordHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Account a SET a.refreshToken = :refreshToken WHERE a.id = :id")
    int updateRefreshTokenById(@Param("id") UUID id, @Param("refreshToken") String refreshToken);
}
