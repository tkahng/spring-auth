package com.tkahng.spring_auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordTests {
    @Test
    public void givenRawPassword_whenEncodedWithArgon2_thenMatchesEncodedPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String result = encoder.encode("myPassword");
        assertTrue(encoder.matches("myPassword", result));

    }
}
