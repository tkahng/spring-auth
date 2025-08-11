package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Token;
import com.tkahng.spring_auth.dto.CreateTokenDto;
import com.tkahng.spring_auth.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final TokenRepository tokenRepository;

    public void saveToken(@NotNull CreateTokenDto createTokenDto) {
        tokenRepository.save(Token.builder()
                .identifier(createTokenDto.getIdentifier())
                .value(createTokenDto.getValue())
                .expires(OffsetDateTime.now()
                        .plusDays(7))
                .build());
    }

    public void deleteToken(String value) {
        tokenRepository.deleteByValue(value);
    }

    @Override
    public String generateRefreshToken(@NotNull CreateTokenDto createTokenDto) {
        var token = tokenRepository.save(Token.builder()
                .identifier(createTokenDto.getIdentifier())
                .value(createTokenDto.getValue())
                .expires(OffsetDateTime.now()
                        .plusDays(7))
                .build());
        return token.getValue();
    }

    @Override
    public String validateRefreshToken(String refreshToken) throws Exception {
        var token = tokenRepository.findByValueAndExpiresAfter(refreshToken, OffsetDateTime.now())
                .orElse(null);
        if (token == null) {
            throw new Exception("Invalid refresh token");
        }
        var identifier = token.getIdentifier();
        tokenRepository.deleteById(token.getId());
        return identifier;
    }
}
