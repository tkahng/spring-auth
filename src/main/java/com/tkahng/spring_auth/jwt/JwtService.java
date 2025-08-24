package com.tkahng.spring_auth.jwt;

import com.tkahng.spring_auth.dto.JwtDto;

public interface JwtService {
    String generateToken(JwtDto dto);
}
