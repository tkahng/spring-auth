package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;

import java.util.Optional;

public interface AuthService {
    Optional<User> findUserByEmail(String email);
    Optional<Account> findAccountByUserIdAndProviderId(String userId, String accountId);
}
