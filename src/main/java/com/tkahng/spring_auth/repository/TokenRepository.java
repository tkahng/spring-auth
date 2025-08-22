package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.Token;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID>, JpaSpecificationExecutor<Token> {
    Optional<Token> findByValueAndExpiresAfter(String value, OffsetDateTime expiresAt);

    Optional<Token> findByValueAndTypeAndExpiresAfter(String value, String type, OffsetDateTime expiresAt);

    @Transactional
    void deleteByValue(String value);

    int deleteByIdentifierAndType(String identifier, String type);

    Page<Token> findByIdentifier(String identifier, Pageable pageable);
}
