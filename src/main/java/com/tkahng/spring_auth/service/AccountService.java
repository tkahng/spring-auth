package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    Account createAccount(@NotNull Account account);

    Optional<Account> findByUserIdAndProviderId(UUID userId, String providerId);

    Optional<Account> findById(UUID id);

    void deleteAccount(Account account);
    
    int updatePasswordById(UUID id, String passwordHash);
}
