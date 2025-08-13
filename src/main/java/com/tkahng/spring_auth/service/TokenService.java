package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.dto.CreateTokenDto;

public interface TokenService {
    /**
     * @param createTokenDto tokenDto
     * @return value used for token
     */
    String generateToken(CreateTokenDto createTokenDto);

    /**
     * @param token token value
     * @param type  type
     * @return identifier of user, email.
     * @throws IllegalArgumentException if token is invalid
     */
    String validate(String token, String type) throws IllegalArgumentException;

    String generateRefreshToken(String identifier);

    String validateRefreshToken(String refreshToken) throws IllegalArgumentException;

    String generateEmailVerificationToken(String identifier);

    String validateEmailVerificationToken(String token) throws IllegalArgumentException;
}
