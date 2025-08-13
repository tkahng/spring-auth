package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.domain.UserAccount;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.dto.AuthenticationResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthService {
    Optional<User> findUserByEmail(String email);

    UserAccount findUserAccountByEmailAndProviderId(String email, String providerId);

    User createUser(@NotNull AuthDto authDto);

    Account createAccount(@NotNull AuthDto authDto, User user);

    /**
     * creates a new user and new account.
     *
     * @param authDto authDto
     * @return UserAccount
     */
    UserAccount createUserAndAccount(@NotNull AuthDto authDto);

    /**
     * creates a new account for an existing user.
     * this method is called when account linking is happening.
     *
     * @param authDto authDto
     * @param user    existing user
     * @return UserAccount
     */
    UserAccount createAccountFromUser(@NotNull AuthDto authDto, User user);

    /**
     * this method is called when a new user signs up, creating a new user and a new account.
     * this method should also call any other methods that are related to a new user signup,
     * such as sending a welcome email to the user.
     *
     * @param authDto authDto
     * @return UserAccount
     */
    UserAccount signupNewUser(@NotNull AuthDto authDto);


    /**
     * this method is called during account linking, where an existing user signs up with an account provider,
     * such as a user who already signed up with email and password signing up with a social media account.
     * during signup, if a user with the same email already exists, we should consider them the same user and link their accounts,
     * allowing them to sign in whichever way they prefer. however, the following points must be considered:
     * <p>
     * 1. if the incoming account email is not verified, we should not link the account.
     * for example, if a user signs up with Google, and another signs up with email and password using the same email,
     * we should not link the accounts as they might not be the same user, and throw an error.
     * <p>
     * 2. if the incoming account email is verified, but the existing user is not verified, we should reset the credentials of the existing account.
     * for example, if a user signs up with email and password, and another signs up with Google using the same email,
     * the original user may be set as verified, even thought they are not the same user, and we should reset the password and send an email asking them to reset their password.
     * <p>
     * 3. if the incoming account email is verified, and the existing user is verified, we should link the accounts.
     * for example, if a user signs up with email and password, and another signs up with Google using the same email,
     * we should link the accounts as they are the same user, and allow them to sign in whichever way they prefer.
     *
     * @param authDto authDto
     * @param user    existing user
     * @return UserAccount
     */
    UserAccount linkAccount(@NotNull AuthDto authDto, User user);

    AuthenticationResponse generateToken(@NotNull User user) throws Exception;

    AuthenticationResponse login(@NotNull AuthDto authDto) throws Exception;

    AuthenticationResponse signup(@NotNull AuthDto authDto) throws Exception;

    AuthenticationResponse handleRefreshToken(String refreshToken) throws Exception;

    void createSuperUser(String email, String password) throws Exception;

    List<String> getRoleNamesByUserId(UUID userId);

    List<String> getPermissionNamesByUserId(UUID userId);
}
