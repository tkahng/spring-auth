package com.tkahng.spring_auth.users;

import com.tkahng.spring_auth.dto.AuthDto;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    void updateUserEmailVerifiedAt(UUID userId, OffsetDateTime emailVerifiedAt);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(UUID userId);

    User createUser(@NotNull AuthDto authDto);
}
