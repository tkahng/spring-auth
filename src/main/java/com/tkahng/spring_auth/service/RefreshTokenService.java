package com.tkahng.spring_auth.service;

public interface RefreshTokenService {
    String generateRefreshToken(String username);

    String validateRefreshToken(String refreshToken);
}
