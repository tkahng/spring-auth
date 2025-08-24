package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Identity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IdentityService {

    Identity createAccount(@NotNull Identity identity);

    Optional<Identity> findByUserIdAndProviderId(UUID userId, String providerId);

    Optional<Identity> findById(UUID id);

    void deleteAccount(Identity identity);

    int updatePasswordById(UUID id, String passwordHash);

    int updateRefreshTokenById(@Param("id") UUID id, @Param("refreshToken") String refreshToken);

    int updateUpdatedAtById(@Param("id") UUID id, @Param("updatedAt") LocalDateTime updatedAt);
}
