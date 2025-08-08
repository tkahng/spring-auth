package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AuthServiceImpl(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<Account> findAccountByUserIdAndProviderId(String userId, String accountId) {
        return accountRepository.findByUserIdAndProviderId(userId, accountId);
    }
}
