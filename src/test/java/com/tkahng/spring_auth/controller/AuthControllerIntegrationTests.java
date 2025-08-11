package com.tkahng.spring_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkahng.spring_auth.dto.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class AuthControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // for JSON parsing

    @Test
    @Rollback
    void loginAndAccessProtectedEndpoint() throws Exception {
        // 1. Sign up first (or login if user already exists)
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "email": "test@example.com", "password": "pass123" }
                                """))
                .andExpect(status().isOk());

        // 2. Login and capture token
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "email": "test@example.com", "password": "pass123" }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationResponse authResponse =
                objectMapper.readValue(loginResult.getResponse()
                                .getContentAsString(),
                        AuthenticationResponse.class);

        String accessToken = authResponse.getAccessToken();
        assertThat(accessToken).isNotBlank();

        // 3. Call protected endpoint with Bearer token
        mockMvc.perform(get("/api/auth/me") // your protected endpoint
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").exists()); // example assertion
    }
}