package com.tkahng.spring_auth.auth;

import com.tkahng.spring_auth.auth.dto.*;
import com.tkahng.spring_auth.identity.Identity;
import com.tkahng.spring_auth.identity.IdentityService;
import com.tkahng.spring_auth.jwt.JwtDto;
import com.tkahng.spring_auth.jwt.JwtService;
import com.tkahng.spring_auth.mail.MailService;
import com.tkahng.spring_auth.rbac.RbacService;
import com.tkahng.spring_auth.token.TokenService;
import com.tkahng.spring_auth.user.User;
import com.tkahng.spring_auth.user.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final PasswordService passwordService;
    private final IdentityService identityService;
    private final RbacService rbacService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final UserService userService;

    private static void updateAccount(@NotNull AuthDto authDto, Identity existingUserIdentity) {

        if (authDto.getRefreshToken() != null) {
            existingUserIdentity.setRefreshToken(authDto.getRefreshToken());
        }
        if (authDto.getAccessToken() != null) {
            existingUserIdentity.setAccessToken(authDto.getAccessToken());
        }
        if (authDto.getExpiresAt() != null) {
            existingUserIdentity.setExpiresAt(authDto.getExpiresAt());
        }
        if (authDto.getScope() != null) {
            existingUserIdentity.setScope(authDto.getScope());
        }
        if (authDto.getIdToken() != null) {
            existingUserIdentity.setIdToken(authDto.getIdToken());
        }
    }

    @Override
    public UserIdentity findUserAccountByEmailAndProviderId(String email, String providerId) {
        var userAccount = new UserIdentity();
        Optional<User> user = userService.findUserByEmail(email);
        if (user.isEmpty()) {
            return userAccount;
        }
        var userDetail = user.get();
        userAccount.setUser(userDetail);
        Optional<Identity> account = identityService.findByUserIdAndProviderId(userDetail.getId(), providerId);
        if (account.isEmpty()) {
            return userAccount;
        }
        var accountDetail = account.get();
        userAccount.setIdentity(accountDetail);
        return userAccount;
    }

    @Override
    public Identity createAccount(@NotNull AuthDto authDto, User user) {
        var account = new Identity()
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
        return identityService.createAccount(account);
    }

    @Override
    public UserIdentity createUserAndAccount(@NotNull AuthDto authDto) {
        var user = userService.createUser(authDto);
        return createAccountFromUser(authDto, user);
    }

    @Override
    public UserIdentity createAccountFromUser(@NotNull AuthDto authDto, User user) {
        var account = createAccount(authDto, user);
        var userAccount = new UserIdentity();
        userAccount.setUser(user);
        userAccount.setIdentity(account);
        return userAccount;
    }

    @Override
    public UserIdentity signupNewUser(@NotNull AuthDto authDto) {
        UserIdentity userAndAccount = createUserAndAccount(authDto);
        if (authDto.getEmailVerifiedAt() == null) {
            mailService.sendVerificationMail(userAndAccount.getUser());
        }
        return userAndAccount;
    }

    @Override
    public UserIdentity linkAccount(@NotNull AuthDto authDto, @NotNull User user) {
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
        if (userAccount.getIdentity() == null) {
            throw new IllegalStateException("user account not found");
        }
        if (userAccount.getIdentity()
                .getPasswordHash() == null) {
            throw new IllegalStateException("password not found");
        }
        if (!passwordService.matches(
                authDto.getPassword(), userAccount.getIdentity()
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
        if (existingUserAccount.getIdentity() != null) {
            throw new EntityExistsException("user account already exists. please login");
        }
        UserIdentity newUserIdentity;
        // if user does not exist, this is a new signup.
        if (existingUserAccount.getUser() == null) {
            newUserIdentity = signupNewUser(authDto);
        } else {
            // if user exists, link account
            newUserIdentity = linkAccount(authDto, existingUserAccount.getUser());
        }
        // create account
        return generateToken(newUserIdentity.getUser());

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
        if (existingUserAccount.getIdentity() != null) {
            var existingAccount = existingUserAccount.getIdentity();
            updateAccount(authDto, existingAccount);
            var updatedAccount = identityService.createAccount(existingAccount);
            existingUserAccount.setIdentity(updatedAccount);
            return generateToken(existingUserAccount.getUser());
        }
        UserIdentity newUserIdentity;
        // if user does not exist, this is a new signup.
        if (existingUserAccount.getUser() == null) {
            newUserIdentity = signupNewUser(authDto);
        } else {
            // if user exists, link account
            newUserIdentity = linkAccount(authDto, existingUserAccount.getUser());
        }
        // create account
        return generateToken(newUserIdentity.getUser());
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
        var userAccount = identityService.findByUserIdAndProviderId(user.getId(), AuthProvider.CREDENTIALS.toString())
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
        var rowsUpdated = identityService.updatePasswordById(accountId, hashedPassword);
        if (rowsUpdated == 0) {
            throw new IllegalStateException("no accounts were updated");
        }
    }

    @Override
    @Transactional
    public void validateAndUpdatePassword(User user, UpdatePasswordRequest request) {
        var account = identityService.findByUserIdAndProviderId(user.getId(), AuthProvider.CREDENTIALS.toString())
                .orElseThrow(() -> new EntityNotFoundException("user account not found"));
        if (!passwordService.matches(request.getOldPassword(), account.getPasswordHash())) {
            throw new IllegalArgumentException("invalid password");
        }

        updateAccountPassword(account.getId(), request.getNewPassword());
    }
}
