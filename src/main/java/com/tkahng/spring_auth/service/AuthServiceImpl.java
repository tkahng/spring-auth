package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.domain.UserAccount;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.dto.AuthProvider;
import com.tkahng.spring_auth.dto.AuthenticationResponse;
import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RbacService rbacService;
    private final TokenService tokenService;


    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<Account> findAccountByUserIdAndProviderId(UUID userId, String providerId) {
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
        var user = User.builder()
                .email(authDto.getEmail())
                .name(authDto.getName())
                .emailVerifiedAt(authDto.getEmailVerifiedAt())
                .build();
        return userRepository.saveAndFlush(user);
    }


    @Override
    public Account createAccount(@NotNull AuthDto authDto, User user) {
        var account = Account.builder()
                .user(user)
                .providerId(
                        authDto.getProvider()
                                .toString()
                )
                .accountId(authDto.getAccountId())
                .build();
        if (authDto.getPassword() != null) {
            var hashedPassword = passwordService.encode(authDto.getPassword());
            account.setPassword_hash(hashedPassword);
        }
        return accountRepository.saveAndFlush(account);
    }


    @Override
    public UserAccount createUserAndAccount(@NotNull AuthDto authDto) {
        var userAccount = new UserAccount();
        var user = createUser(authDto);
        userAccount.setUser(user);
        var account = createAccount(authDto, user);
        userAccount.setAccount(account);
        return userAccount;
    }

    @Override
    public UserAccount findOrCreateUserAndCreateAccount(@NotNull AuthDto authDto) {
        var userAccount = new UserAccount();
        User user = findUserByEmail(authDto.getEmail()).orElse(null);
        if (user == null) {
            user = createUser(authDto);
        }
        Account account = findAccountByUserIdAndProviderId(user.getId(), authDto.getProvider()
                .toString()).orElse(null);
        if (account == null) {
            account = createAccount(authDto, user);
        }
        userAccount.setUser(user);
        userAccount.setAccount(account);
        return userAccount;
    }

    public AuthenticationResponse generateToken(@NotNull User user) throws Exception {
        var accessToken = jwtService.generateToken(user.getEmail());
        var refreshToken = tokenService.generateRefreshToken(user.getEmail());
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    @Override
    public AuthenticationResponse login(@NotNull AuthDto authDto) throws Exception {
        var userAccount = findUserAccountByEmailAndProviderId(authDto.getEmail(), authDto.getProvider()
                .toString());
        if (userAccount.getUser() == null) {
            throw new Exception("user not found");
        }
        if (userAccount.getAccount() == null) {
            throw new Exception("user account not found");
        }
        if (userAccount.getAccount()
                .getPassword_hash() == null) {
            throw new Exception("password not found");
        }
        if (!passwordService.matches(authDto.getPassword(), userAccount.getAccount()
                .getPassword_hash())) {
            throw new Exception("invalid password");
        }
        return generateToken(userAccount.getUser());
    }

    @Override
    public AuthenticationResponse signup(@NotNull AuthDto authDto) throws Exception {
        var existingUserAccount = findUserAccountByEmailAndProviderId(authDto.getEmail(), authDto.getProvider()
                .toString());

        // check if credentials account already exists
        // if it does, throw error
        if (existingUserAccount.getAccount() != null) {
            throw new Exception("user account already exists. please login");
        }
        User user;
        // check if user already exists. if not, create user
        if (existingUserAccount.getUser() == null) {
            user = createUser(authDto);
        } else {
            user = existingUserAccount.getUser();
        }
        // create account
        var account = createAccount(authDto, user);
        return generateToken(user);

    }

    @Override
    public AuthenticationResponse handleRefreshToken(String refreshToken) throws Exception {
        var identifier = tokenService.validateRefreshToken(refreshToken);
        var user = findUserByEmail(identifier)
                .orElseThrow(() -> new Exception("user not found"));
        return generateToken(user);
    }

    @Override
    public Account createSuperUser(String email, String password) {
        var existingUserAccount = findUserAccountByEmailAndProviderId(email, AuthProvider.CREDENTIALS.toString());
        if (existingUserAccount.getUser() != null) {
            return existingUserAccount.getAccount();
        }
        var userAccount = createUserAndAccount(AuthDto.builder()
                .email(email)
                .password(password)
                .provider(AuthProvider.CREDENTIALS)
                .accountId(email)
                .build());
        return userAccount.getAccount();
    }

}
