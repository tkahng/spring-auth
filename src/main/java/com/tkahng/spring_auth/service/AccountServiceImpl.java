package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Account createAccount(@NotNull Account account) {
        return accountRepository.saveAndFlush(account);
    }

    @Override
    @Transactional
    public Optional<Account> findByUserIdAndProviderId(UUID userId, String providerId) {
        return accountRepository.findByUserIdAndProviderId(userId, providerId);
    }

    @Override
    @Transactional
    public Optional<Account> findById(UUID id) {
        return accountRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    @Override
    @Transactional
    public int updatePasswordById(UUID id, String passwordHash) {
        return accountRepository.updatePasswordById(id, passwordHash);
    }

}
