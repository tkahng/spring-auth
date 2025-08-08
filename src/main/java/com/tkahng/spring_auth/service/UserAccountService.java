package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.domain.UserAccount;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountService  {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public UserAccountService(UserRepository userRepository, AccountRepository accountRepository ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<Account> findAccountByUserIdAndProviderId(String userId, String providerId) {
        return accountRepository.findByUserIdAndProviderId(userId, providerId);
    }

    public UserAccount findUserAccountByEmailAndProviderId(String email, String providerId) {
        var userAccount = new UserAccount();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return userAccount;
        }
        var userDetail = user.get();
        userAccount.setUser(userDetail);
        Optional<Account> account = accountRepository.findByUserIdAndProviderId(userDetail.getId(), providerId);
        if (account.isEmpty()) {
            return userAccount;
        }
        var accountDetail = account.get();
        userAccount.setAccount(accountDetail);
        return userAccount;
    }

    public User createUser(@NotNull AuthDto authDto) {
        var user = new User();
        user.setEmail(authDto.getEmail());
        user.setName(authDto.getName());
        userRepository.save(user);
        return user;
    }

    public UserAccount createUserAccount(@NotNull AuthDto authDto) {
        var existingUserAccount = findUserAccountByEmailAndProviderId(authDto.getEmail(), authDto.getProviderId());
        if (existingUserAccount.getUser() != null) {
            throw new EntityExistsException("User already exists");
        }
        var user = new User();
        user.setEmail(authDto.getEmail());
        var account = new Account();
        account.setProviderId(authDto.getProviderId());
        account.setPassword(authDto.getPassword());
        user.setAccounts(new java.util.ArrayList<>());
        user.getAccounts().add(account);
        userRepository.save(user);
        return new UserAccount(user, account);
    }
}
