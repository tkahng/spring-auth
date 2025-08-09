package com.tkahng.spring_auth.repository;


import com.tkahng.spring_auth.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUserIdAndProviderId(UUID userId, String providerId);
}
