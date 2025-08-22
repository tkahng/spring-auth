package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.*;
import com.tkahng.spring_auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
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
    public UserAccount signupNewUser(@NotNull AuthDto authDto) throws Exception {
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
    public AuthenticationResponse generateToken(@NotNull User user) throws Exception {
        var roles = getRoleNamesByUserId(user.getId());
        var permissions = getPermissionNamesByUserId(user.getId());
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
    public AuthenticationResponse credentialsLogin(@NotNull AuthDto authDto) throws Exception {
        var userAccount = findUserAccountByEmailAndProviderId(
                authDto.getEmail(), authDto.getProvider()
                        .toString()
        );
        if (userAccount.getUser() == null) {
            throw new Exception("user not found");
        }
        if (userAccount.getAccount() == null) {
            throw new Exception("user account not found");
        }
        if (userAccount.getAccount()
                .getPasswordHash() == null) {
            throw new Exception("password not found");
        }
        if (!passwordService.matches(
                authDto.getPassword(), userAccount.getAccount()
                        .getPasswordHash()
        )) {
            throw new Exception("invalid password");
        }
        return generateToken(userAccount.getUser());
    }

    @Override
    public AuthenticationResponse credentialsSignup(@NotNull AuthDto authDto) throws Exception {
        var existingUserAccount = findUserAccountByEmailAndProviderId(
                authDto.getEmail(), authDto.getProvider()
                        .toString()
        );

        // check if credentials account already exists
        // if it does, throw error
        if (existingUserAccount.getAccount() != null) {
            throw new Exception("user account already exists. please login");
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
    public AuthenticationResponse oauth2Login(@NotNull AuthDto authDto) throws Exception {
        var existingUserAccount = findUserAccountByEmailAndProviderId(
                authDto.getEmail(), authDto.getProvider()
                        .toString()
        );

        // check if credentials account already exists
        // if it does, throw error
        if (existingUserAccount.getAccount() != null) {
            throw new Exception("user account already exists. please login");
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
    public AuthenticationResponse handleRefreshToken(String refreshToken) throws Exception {
        var identifier = tokenService.validateRefreshToken(refreshToken);
        var user = userService.findUserByEmail(identifier)
                .orElseThrow(() -> new Exception("user not found"));
        return generateToken(user);
    }

    @Override
    public void handleEmailVerification(String token) throws Exception {
        var identifier = tokenService.validateEmailVerificationToken(token);
        var user = userService.findUserByEmail(identifier)
                .orElseThrow(() -> new Exception("user not found"));
        userService.updateUserEmailVerifiedAt(user.getId(), OffsetDateTime.now());
    }

    @Override
    public void createSuperUser(String email, String password) throws Exception {
        var existingUserAccount = findUserAccountByEmailAndProviderId(email, AuthProvider.CREDENTIALS.toString());
        if (existingUserAccount.getUser() != null) {
            throw new Exception("user already exists");
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
    public List<String> getRoleNamesByUserId(UUID userId) {
        return rbacService.findAllRoles(
                        RoleFilter.builder()
                                .userId(userId)
                                .build(), Pageable.unpaged()
                )
                .stream()
                .map(Role::getName)
                .toList();
    }

    @Override
    public List<String> getPermissionNamesByUserId(UUID userId) {
        return rbacService.findAllPermissions(
                        PermissionFilter.builder()
                                .userId(userId)
                                .build(), Pageable.unpaged()
                )
                .stream()
                .map(Permission::getName)
                .toList();
    }

}
