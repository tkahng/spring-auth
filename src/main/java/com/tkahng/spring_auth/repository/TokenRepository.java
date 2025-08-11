package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID>, JpaSpecificationExecutor<Token> {
    Optional<Token> findByValueAndExpiresAfter(String value, OffsetDateTime expiresAt);

    void deleteByValue(String value);
}
