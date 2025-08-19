package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Token;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.CreateTokenDto;
import com.tkahng.spring_auth.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {
    private static final String TOKEN_REFRESH_TYPE = "refresh_token";
    private static final int TOKEN_REFRESH_TTL = 604800;
    private static final String TOKEN_TYPE_EMAIL_VERIFICATION = "email_verification";
    private static final int TOKEN_EMAIL_VERIFICATION_TTL = 86400;
    private final TokenRepository tokenRepository;

    private String generateValue() {
        return UUID.randomUUID()
                .toString();
    }

    @Override
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

    @Transactional
    public Optional<Token> findByValueAndTypeAndExpiresAfter(String value, String type, OffsetDateTime expiresAt) {
        return tokenRepository.findByValueAndTypeAndExpiresAfter(value, type, expiresAt);
    }

    @Override
    public String validate(String value, String type) throws IllegalArgumentException {
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
        var value = generateValue();
        var dto = CreateTokenDto.builder()
                .identifier(identifier)
                .type(TOKEN_REFRESH_TYPE)
                .ttl(TOKEN_REFRESH_TTL)
                .value(value)
                .build();
        return generateToken(dto);
    }

    @Override
    public String validateRefreshToken(String refreshToken) throws IllegalArgumentException {
        return validate(refreshToken, TOKEN_REFRESH_TYPE);
    }

    @Override
    public String generateEmailVerificationToken(String identifier) {
        var value = generateValue();
        var dto = CreateTokenDto.builder()
                .identifier(identifier)
                .type(TOKEN_TYPE_EMAIL_VERIFICATION)
                .ttl(TOKEN_EMAIL_VERIFICATION_TTL)
                .value(value)
                .build();
        return generateToken(dto);
    }

    @Override
    public String validateEmailVerificationToken(String token) throws IllegalArgumentException {
        return validate(token, TOKEN_TYPE_EMAIL_VERIFICATION);
    }

    private String getText(User user, String verificationId) {
        String encodedVerificationId = new String(Base64.getEncoder()
                .encode(verificationId.getBytes()));

        return "Dear " +
                user.getEmail() +
                "," +
                System.lineSeparator() +
                System.lineSeparator() +
                "Your account has been successfully created in the Course Tracker application. " +
                "Activate your account by clicking the following link: http://localhost:8080/verify/email?id=" +
                encodedVerificationId +
                System.lineSeparator() +
                System.lineSeparator() +
                "Regards," +
                System.lineSeparator() +
                "Course Tracker Team";
    }
}
