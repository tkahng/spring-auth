package com.tkahng.spring_auth.users;

import com.tkahng.spring_auth.auth.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Optional<User> findUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public User createUser(@NotNull AuthDto authDto) {
        var user = User.builder()
                .email(authDto.getEmail())
                .name(authDto.getName())
                .emailVerifiedAt(authDto.getEmailVerifiedAt())
                .build();
        return userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional
    public void updateUserEmailVerifiedAt(UUID userId, OffsetDateTime emailVerifiedAt) {
        userRepository.updateEmailVerifiedAt(userId, emailVerifiedAt);
    }
}
