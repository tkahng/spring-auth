package com.tkahng.spring_auth.identity;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentityServiceImpl implements IdentityService {
    private final IdentityRepository identityRepository;

    @Override
    @Transactional
    public Identity createAccount(@NotNull Identity identity) {
        return identityRepository.saveAndFlush(identity);
    }

    @Override
    @Transactional
    public Optional<Identity> findByUserIdAndProviderId(UUID userId, String providerId) {
        return identityRepository.findByUserIdAndProviderId(userId, providerId);
    }

    @Override
    @Transactional
    public Optional<Identity> findById(UUID id) {
        return identityRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteAccount(Identity identity) {
        identityRepository.delete(identity);
    }

    @Override
    @Transactional
    public int updatePasswordById(UUID id, String passwordHash) {
        return identityRepository.updatePasswordById(id, passwordHash);
    }

    @Override
    @Transactional
    public int updateRefreshTokenById(UUID id, String refreshToken) {
        return identityRepository.updateRefreshTokenById(id, refreshToken);
    }

    @Override
    @Transactional
    public int updateUpdatedAtById(UUID id, LocalDateTime updatedAt) {
        return identityRepository.updateUpdatedAtById(id, updatedAt);
    }

}
