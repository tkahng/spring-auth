package com.tkahng.spring_auth.service;

public interface JwtService {
    String generateToken(String username);
}
