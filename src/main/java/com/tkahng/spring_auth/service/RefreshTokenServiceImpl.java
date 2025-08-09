package com.tkahng.spring_auth.service;

import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Override
    public String generateRefreshToken(String username) {
        return username;
    }

    @Override
    public String validateRefreshToken(String refreshToken) {
        return refreshToken;
    }
}
