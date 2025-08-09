package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.domain.UserAccount;
import com.tkahng.spring_auth.dto.AuthDto;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface UserAccountService {
    Optional<User> findUserByEmail(String email);

    Optional<Account> findAccountByUserIdAndProviderId(String userId, String providerId);

    UserAccount findUserAccountByEmailAndProviderId(String email, String providerId);

    User createUser(@NotNull AuthDto authDto);

    Account createAccount(@NotNull AuthDto authDto, User user);

    UserAccount createUserAccount(@NotNull AuthDto authDto);
}
