package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.auth.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class PasswordServiceImplTests {
    private final PasswordService passwordService;

    @Autowired
    public PasswordServiceImplTests(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Test
    public void givenRawPassword_whenEncodedWithArgon2_thenMatchesEncodedPassword() {
        String result = passwordService.encode("myPassword");
        assertTrue(passwordService.matches("myPassword", result));

    }
}
