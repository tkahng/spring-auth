package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AuthServiceTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testSignupSuccess() {

    }
}