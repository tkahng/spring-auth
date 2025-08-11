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
public class TokenServiceImpl {
    private final TokenRepository tokenRepository;

    public void saveToken(@NotNull CreateTokenDto createTokenDto) {
        tokenRepository.saveAndFlush(Token.builder()
                .identifier(createTokenDto.getIdentifier())
                .value(createTokenDto.getValue())
                .expires(OffsetDateTime.now()
                        .plusDays(7))
                .build());
    }

    public void deleteToken(String value) {
        tokenRepository.deleteByValue(value);
    }

}
