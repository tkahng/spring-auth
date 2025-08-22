package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Token;
import com.tkahng.spring_auth.dto.CreateTokenDto;
import com.tkahng.spring_auth.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {
    private static final String TOKEN_REFRESH_TYPE = "refresh_token";
    private static final int TOKEN_REFRESH_TTL = 604800;
    private static final String TOKEN_TYPE_EMAIL_VERIFICATION = "email_verification";
    private static final int TOKEN_EMAIL_VERIFICATION_TTL = 86400;
    private static final String TOKEN_TYPE_PASSWORD_RESET = "password_reset";
    private static final int TOKEN_PASSWORD_RESET_TTL = 86400;
    private final TokenRepository tokenRepository;

    private String generateValue() {
        return UUID.randomUUID()
                .toString();
    }

    @Override
    @Transactional
    public String generateToken(CreateTokenDto createTokenDto) {
        var token = tokenRepository.saveAndFlush(Token.builder()
                .identifier(createTokenDto.getIdentifier())
                .type(createTokenDto.getType())
                .value(createTokenDto.getValue())
                .expires(OffsetDateTime.now()
                        .plusSeconds(createTokenDto.getTtl()))
                .build());
        return token.getValue();
    }

    @Override
    @Transactional
    public String createToken(String identifier, String type, int ttl) {
        var value = generateValue();
        var token = tokenRepository.saveAndFlush(Token.builder()
                .identifier(identifier)
                .type(type)
                .value(value)
                .expires(OffsetDateTime.now()
                        .plusSeconds(ttl))
                .build());
        return token.getValue();
    }

    @Transactional
    public Optional<Token> findByValueAndTypeAndExpiresAfter(String value, String type, OffsetDateTime expiresAt) {
        return tokenRepository.findByValueAndTypeAndExpiresAfter(value, type, expiresAt);
    }

    @Override
    public String validate(String value, String type) {
        var result = findByValueAndTypeAndExpiresAfter(value, type, OffsetDateTime.now())
                .orElse(null);
        if (result == null) {
            throw new IllegalArgumentException("Invalid token. ");
        }
        var identifier = result.getIdentifier();
        tokenRepository.deleteById(result.getId());
        return identifier;
    }

    @Override
    public String generateRefreshToken(String identifier) {
        return createToken(identifier, TOKEN_REFRESH_TYPE, TOKEN_REFRESH_TTL);
    }

    @Override
    public String validateRefreshToken(String refreshToken) {
        return validate(refreshToken, TOKEN_REFRESH_TYPE);
    }

    @Override
    public String generateEmailVerificationToken(String identifier) {
        deleteByIdentifierAndType(identifier, TOKEN_TYPE_EMAIL_VERIFICATION);
        return createToken(identifier, TOKEN_TYPE_EMAIL_VERIFICATION, TOKEN_EMAIL_VERIFICATION_TTL);
    }

    @Override
    public String validateEmailVerificationToken(String token) {
        return validate(token, TOKEN_TYPE_EMAIL_VERIFICATION);
    }

    @Override
    public String generatePasswordResetToken(String identifier) {
        deleteByIdentifierAndType(identifier, TOKEN_TYPE_PASSWORD_RESET);
        return createToken(identifier, TOKEN_TYPE_PASSWORD_RESET, TOKEN_PASSWORD_RESET_TTL);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        return validate(token, TOKEN_TYPE_PASSWORD_RESET);
    }

    @Override
    @Transactional
    public int deleteByIdentifierAndType(String identifier, String type) {
        return tokenRepository.deleteByIdentifierAndType(identifier, type);
    }


    @Override
    @Transactional
    public Page<Token> findByIdentifier(String identifier, Pageable pageable) {
        return tokenRepository.findByIdentifier(identifier, pageable);
    }

    @Override
    @Transactional
    public Optional<Token> findByValue(String value) {
        return tokenRepository.findByValue(value);
    }
}
