package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Token;
import com.tkahng.spring_auth.dto.CreateTokenDto;
import com.tkahng.spring_auth.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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

    @Override
    public String validate(String value, String type) throws IllegalArgumentException {
        var result = tokenRepository.findByValueAndTypeAndExpiresAfter(value, type, OffsetDateTime.now())
                .orElse(null);
        if (result == null) {
            throw new IllegalArgumentException("Invalid refresh token");
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
}
