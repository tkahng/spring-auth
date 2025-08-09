package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.domain.UserAccount;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AuthServiceImpl(PasswordService passwordService, UserRepository userRepository, AccountRepository accountRepository) {
        this.passwordService = passwordService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<Account> findAccountByUserIdAndProviderId(String userId, String providerId) {
        return accountRepository.findByUserIdAndProviderId(userId, providerId);
    }

    @Override
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

    @Override
    public User createUser(@NotNull AuthDto authDto) {
        var user = new User();
        user.setEmail(authDto.getEmail());
        user.setName(authDto.getName());
        return userRepository.save(user);
    }

   
    @Override
    public Account createAccount(@NotNull AuthDto authDto, User user) {
        var account = new Account();
        account.setUser(user);
        account.setProviderId(authDto.getProvider().toString());
        account.setAccountId(authDto.getAccountId());
        if (authDto.getPassword() != null) {
            var hashedPassword = passwordService.encode(authDto.getPassword());
            account.setPassword(hashedPassword);
        }
        return accountRepository.save(account);
    }

    @Override
    public UserAccount createUserAccount(@NotNull AuthDto authDto) {
        var userAccount = new UserAccount();
        var user = createUser(authDto);
        userAccount.setUser(user);
        var account = createAccount(authDto, user);
        userAccount.setAccount(account);
        return userAccount;
    }
}
