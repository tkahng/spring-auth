package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Token;
import com.tkahng.spring_auth.dto.CreateTokenDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TokenService {
    /**
     * @param createTokenDto tokenDto
     * @return value used for token
     */
    String generateToken(CreateTokenDto createTokenDto);

    String createToken(String identifier, String type, int ttl);

    /**
     * @param token token value
     * @param type  type
     * @return identifier of user, email.
     */
    String validate(String token, String type);

    String generateRefreshToken(String identifier);

    String validateRefreshToken(String refreshToken);

    String generateEmailVerificationToken(String identifier);

    String validateEmailVerificationToken(String token);

    String generatePasswordResetToken(String identifier);

    String validatePasswordResetToken(String token);

    int deleteByIdentifierAndType(String identifier, String type);

    Page<Token> findByIdentifier(String identifier, Pageable pageable);

    Optional<Token> findByValue(String value);
}
