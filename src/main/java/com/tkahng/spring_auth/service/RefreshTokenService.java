package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.dto.CreateTokenDto;

public interface RefreshTokenService {
    String generateRefreshToken(CreateTokenDto createTokenDto);

    /**
     * @param refreshToken refreshToken
     * @return identifier of user, email.
     * @throws Exception if refresh token is invalid
     */
    String validateRefreshToken(String refreshToken) throws Exception;
}
