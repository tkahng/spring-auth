package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Identity;
import com.tkahng.spring_auth.users.User;
import com.tkahng.spring_auth.domain.UserIdentity;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.dto.AuthenticationResponse;
import com.tkahng.spring_auth.dto.SetPasswordRequest;
import com.tkahng.spring_auth.dto.UpdatePasswordRequest;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface AuthService {
    // CRUD Methods -------------------------------------------------------------
    //Optional<User> findUserByEmail(String email);

    UserIdentity findUserAccountByEmailAndProviderId(String email, String providerId);

    //User createUser(@NotNull AuthDto authDto);

    Identity createAccount(@NotNull AuthDto authDto, User user);

    /**
     * creates a new user and new account.
     *
     * @param authDto authDto
     * @return UserAccount
     */
    UserIdentity createUserAndAccount(@NotNull AuthDto authDto);

    /**
     * creates a new account for an existing user.
     * this method is called when account linking is happening.
     *
     * @param authDto authDto
     * @param user    existing user
     * @return UserAccount
     */
    UserIdentity createAccountFromUser(@NotNull AuthDto authDto, User user);

    /**
     * this method is called when a new user signs up, creating a new user and a new account.
     * this method should also call any other methods that are related to a new user signup,
     * such as sending a welcome email to the user.
     *
     * @param authDto authDto
     * @return UserAccount
     */
    UserIdentity signupNewUser(@NotNull AuthDto authDto);


    /**
     * this method is called during account linking, where an existing user signs up with an account provider,
     * such as a user who already signed up with email and password signing up with a social media account.
     * during signup, if a user with the same email already exists, we should consider them the same user and link
     * their accounts,
     * allowing them to sign in whichever way they prefer. however, the following points must be considered:
     * <p>
     * 1. if the incoming account email is not verified, we should not link the account.
     * for example, if a user signs up with Google, and another signs up with email and password using the same email,
     * we should not link the accounts as they might not be the same user, and throw an error.
     * <p>
     * 2. if the incoming account email is verified, but the existing user is not verified, we should reset the
     * credentials of the existing account.
     * for example, if a user signs up with email and password, and another signs up with Google using the same email,
     * the original user may be set as verified, even thought they are not the same user, and we should reset the
     * password and send an email asking them to reset their password.
     * <p>
     * 3. if the incoming account email is verified, and the existing user is verified, we should link the accounts.
     * for example, if a user signs up with email and password, and another signs up with Google using the same email,
     * we should link the accounts as they are the same user, and allow them to sign in whichever way they prefer.
     *
     * @param authDto authDto
     * @param user    existing user
     * @return UserAccount
     */
    UserIdentity linkAccount(@NotNull AuthDto authDto, @NotNull User user);

    /**
     * generates a new token for the user.
     *
     * @param user user
     * @return AuthenticationResponse
     */
    AuthenticationResponse generateToken(@NotNull User user);

    AuthenticationResponse credentialsLogin(@NotNull AuthDto authDto);

    AuthenticationResponse credentialsSignup(@NotNull AuthDto authDto);

    AuthenticationResponse oauth2Login(@NotNull AuthDto authDto);

    void createSuperUser(String email, String password);

    void setPassword(@NotNull User user, @NotNull SetPasswordRequest request);

    void updateAccountPassword(UUID accountId, String password);

    void validateAndUpdatePassword(User user, UpdatePasswordRequest request);


}
