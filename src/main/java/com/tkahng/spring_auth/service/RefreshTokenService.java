package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.dto.CreateTokenDto;

public interface RefreshTokenService {
    String generateRefreshToken(CreateTokenDto createTokenDto);

    String validateRefreshToken(String refreshToken) throws Exception;
}
