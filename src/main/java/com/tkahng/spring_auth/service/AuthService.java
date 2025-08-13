package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.domain.UserAccount;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.dto.AuthenticationResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface AuthService {
    Optional<User> findUserByEmail(String email);

    Optional<Account> findAccountByUserIdAndProviderId(UUID userId, String providerId);

    UserAccount findUserAccountByEmailAndProviderId(String email, String providerId);

    User createUser(@NotNull AuthDto authDto);

    Account createAccount(@NotNull AuthDto authDto, User user);

    UserAccount createUserAndAccount(@NotNull AuthDto authDto);

    UserAccount findOrCreateUserAndCreateAccount(@NotNull AuthDto authDto);

    AuthenticationResponse generateToken(@NotNull User user) throws Exception;

    AuthenticationResponse login(@NotNull AuthDto authDto) throws Exception;

    AuthenticationResponse signup(@NotNull AuthDto authDto) throws Exception;

    AuthenticationResponse handleRefreshToken(String refreshToken) throws Exception;

    Account createSuperUser(String email, String password);
}
