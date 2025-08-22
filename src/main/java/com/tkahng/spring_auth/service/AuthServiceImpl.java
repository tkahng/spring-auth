package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.domain.UserAccount;
import com.tkahng.spring_auth.dto.*;
import jakarta.persistence.EntityExistsException;
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
    private final AccountService accountService;
    private final RbacService rbacService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final UserService userService;

    private static void updateAccount(@NotNull AuthDto authDto, Account existingUserAccount) {

        if (authDto.getRefreshToken() != null) {
            existingUserAccount.setRefreshToken(authDto.getRefreshToken());
        }
        if (authDto.getAccessToken() != null) {
            existingUserAccount.setAccessToken(authDto.getAccessToken());
        }
        if (authDto.getExpiresAt() != null) {
            existingUserAccount.setExpiresAt(authDto.getExpiresAt());
        }
        if (authDto.getScope() != null) {
            existingUserAccount.setScope(authDto.getScope());
        }
        if (authDto.getIdToken() != null) {
            existingUserAccount.setIdToken(authDto.getIdToken());
        }
    }

    @Override
    public UserAccount findUserAccountByEmailAndProviderId(String email, String providerId) {
        var userAccount = new UserAccount();
        Optional<User> user = userService.findUserByEmail(email);
        if (user.isEmpty()) {
            return userAccount;
        }
        var userDetail = user.get();
        userAccount.setUser(userDetail);
        Optional<Account> account = accountService.findByUserIdAndProviderId(userDetail.getId(), providerId);
        if (account.isEmpty()) {
            return userAccount;
        }
        var accountDetail = account.get();
        userAccount.setAccount(accountDetail);
        return userAccount;
    }

    @Override
    public Account createAccount(@NotNull AuthDto authDto, User user) {
        var account = new Account()
                .setUser(user)
                .setIdToken(authDto.getIdToken())
                .setScope(authDto.getScope())
                .setSessionState(authDto.getSessionState())
                .setRefreshToken(authDto.getRefreshToken())
                .setAccessToken(authDto.getAccessToken())
                .setExpiresAt(authDto.getExpiresAt())
                .setProviderId(
                        authDto.getProvider()
                                .toString()
                )
                .setAccountId(authDto.getAccountId());
        if (authDto.getPassword() != null) {
            var hashedPassword = passwordService.encode(authDto.getPassword());
            account.setPasswordHash(hashedPassword);
        }
        return accountService.createAccount(account);
    }

    @Override
    public UserAccount createUserAndAccount(@NotNull AuthDto authDto) {
        var user = userService.createUser(authDto);
        return createAccountFromUser(authDto, user);
    }

    @Override
    public UserAccount createAccountFromUser(@NotNull AuthDto authDto, User user) {
        var account = createAccount(authDto, user);
        var userAccount = new UserAccount();
        userAccount.setUser(user);
        userAccount.setAccount(account);
        return userAccount;
    }

    @Override
    public UserAccount signupNewUser(@NotNull AuthDto authDto) {
        UserAccount userAndAccount = createUserAndAccount(authDto);
        if (authDto.getEmailVerifiedAt() == null) {
            mailService.sendVerificationMail(userAndAccount.getUser());
        }
        return userAndAccount;
    }

    @Override
    public UserAccount linkAccount(@NotNull AuthDto authDto, @NotNull User user) {
        return createAccountFromUser(authDto, user);
    }

    @Override
    public AuthenticationResponse generateToken(@NotNull User user) {
        var roles = rbacService.getRoleNamesByUserId(user.getId());
        var permissions = rbacService.getPermissionNamesByUserId(user.getId());
        var accessToken = jwtService.generateToken(JwtDto.builder()
                .email(user.getEmail())
                .userId(user.getId())
                .roles(roles)
                .permissions(permissions)
                .emailVerifiedAt(user.getEmailVerifiedAt())
                .build());
        var refreshToken = tokenService.generateRefreshToken(user.getEmail());
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    @Override
    public AuthenticationResponse credentialsLogin(@NotNull AuthDto authDto) {
        var userAccount = findUserAccountByEmailAndProviderId(
                authDto.getEmail(), authDto.getProvider()
                        .toString()
        );
        if (userAccount.getUser() == null) {
            throw new IllegalStateException("user not found");
        }
        if (userAccount.getAccount() == null) {
            throw new IllegalStateException("user account not found");
        }
        if (userAccount.getAccount()
                .getPasswordHash() == null) {
            throw new IllegalStateException("password not found");
        }
        if (!passwordService.matches(
                authDto.getPassword(), userAccount.getAccount()
                        .getPasswordHash()
        )) {
            throw new IllegalArgumentException("invalid password");
        }
        return generateToken(userAccount.getUser());
    }

    @Override
    public AuthenticationResponse credentialsSignup(@NotNull AuthDto authDto) {
        var existingUserAccount = findUserAccountByEmailAndProviderId(
                authDto.getEmail(), authDto.getProvider()
                        .toString()
        );

        // check if credentials account already exists
        // if it does, throw error
        if (existingUserAccount.getAccount() != null) {
            throw new EntityExistsException("user account already exists. please login");
        }
        UserAccount newUserAccount;
        // if user does not exist, this is a new signup.
        if (existingUserAccount.getUser() == null) {
            newUserAccount = signupNewUser(authDto);
        } else {
            // if user exists, link account
            newUserAccount = linkAccount(authDto, existingUserAccount.getUser());
        }
        // create account
        return generateToken(newUserAccount.getUser());

    }

    @Override
    public AuthenticationResponse oauth2Login(@NotNull AuthDto authDto) {
        if (authDto.getProvider() == AuthProvider.CREDENTIALS) {
            throw new IllegalArgumentException("provider cannot be credentials");
        }

        var existingUserAccount = findUserAccountByEmailAndProviderId(
                authDto.getEmail(), authDto.getProvider()
                        .toString()
        );

        // check if oauth account already exists
        // if it does, update account
        if (existingUserAccount.getAccount() != null) {
            var existingAccount = existingUserAccount.getAccount();
            updateAccount(authDto, existingAccount);
            var updatedAccount = accountService.createAccount(existingAccount);
            existingUserAccount.setAccount(updatedAccount);
            return generateToken(existingUserAccount.getUser());
        }
        UserAccount newUserAccount;
        // if user does not exist, this is a new signup.
        if (existingUserAccount.getUser() == null) {
            newUserAccount = signupNewUser(authDto);
        } else {
            // if user exists, link account
            newUserAccount = linkAccount(authDto, existingUserAccount.getUser());
        }
        // create account
        return generateToken(newUserAccount.getUser());
    }

    @Override
    public void createSuperUser(String email, String password) {
        var existingUserAccount = findUserAccountByEmailAndProviderId(email, AuthProvider.CREDENTIALS.toString());
        if (existingUserAccount.getUser() != null) {
            throw new EntityExistsException("user already exists");
        }
        var userAccount = createUserAndAccount(AuthDto.builder()
                .email(email)
                .password(password)
                .provider(AuthProvider.CREDENTIALS)
                .accountId(email)
                .build());
        var role = rbacService.findOrCreateRoleByName("admin");
        rbacService.assignRoleToUser(userAccount.getUser(), role);
    }


    @Override
    public void setPassword(@NotNull User user, @NotNull SetPasswordRequest request) {
        var userAccount = accountService.findByUserIdAndProviderId(user.getId(), AuthProvider.CREDENTIALS.toString())
                .orElse(null);
        if (userAccount != null) {
            throw new IllegalStateException("cannot set password on existing credentials account");
        }
        createAccountFromUser(
                new AuthDto().setPassword(request.getPassword())
                        .setEmail(user.getEmail())
                        .setProvider(AuthProvider.CREDENTIALS)
                        .setAccountId(user.getEmail()), user
        );
    }

    @Override
    public void updateAccountPassword(UUID accountId, String password) {
        var hashedPassword = passwordService.encode(password);
        var rowsUpdated = accountService.updatePasswordById(accountId, hashedPassword);
        if (rowsUpdated == 0) {
            throw new IllegalStateException("no accounts were updated");
        }
    }
}
