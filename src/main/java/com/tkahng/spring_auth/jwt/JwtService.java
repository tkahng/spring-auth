package com.tkahng.spring_auth.jwt;

public interface JwtService {
    String generateToken(JwtDto dto);
}
