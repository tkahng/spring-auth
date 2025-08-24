package com.tkahng.spring_auth.identity;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, UUID> {
    Optional<Identity> findByUserIdAndProviderId(UUID userId, String providerId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Identity a SET a.passwordHash = :passwordHash WHERE a.id = :id")
    int updatePasswordById(@Param("id") UUID id, @Param("passwordHash") String passwordHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Identity a SET a.refreshToken = :refreshToken WHERE a.id = :id")
    int updateRefreshTokenById(@Param("id") UUID id, @Param("refreshToken") String refreshToken);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Identity a SET a.updatedAt = :updatedAt WHERE a.id = :id")
    int updateUpdatedAtById(@Param("id") UUID id, @Param("updatedAt") LocalDateTime updatedAt);
}
