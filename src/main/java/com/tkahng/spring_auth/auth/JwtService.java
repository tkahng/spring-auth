package com.tkahng.spring_auth.auth;

import com.tkahng.spring_auth.dto.JwtDto;

public interface JwtService {
    String generateToken(JwtDto dto);
}
