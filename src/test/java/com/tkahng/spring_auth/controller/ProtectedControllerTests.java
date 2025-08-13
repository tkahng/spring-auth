package com.tkahng.spring_auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.dto.AuthProvider;
import com.tkahng.spring_auth.dto.JwtDto;
import com.tkahng.spring_auth.service.AuthService;
import com.tkahng.spring_auth.service.JwtService;
import com.tkahng.spring_auth.service.RbacService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class ProtectedControllerTests {
    @Autowired
    private AuthService authService;
    @Autowired
    private RbacService rbacService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // for JSON parsing


    @Test
    @Rollback
    public void testBasicRole() throws Exception {
        var user = createUserWithRole("basic");
        var roles = authService.getRoleNamesByUserId(user.getId());
        var token = jwtService.generateToken(JwtDto.builder()
                .email(user.getEmail())
                .roles(roles)
                .build());
        mockMvc.perform(get("/api/protected/basic")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

    }

    private User createUserWithRole(String roleName) {
        rbacService.initRolesAndPermissions();
        var user = authService.createUserAndAccount(AuthDto.builder()
                .email("test@test.com")
                .name("test")
                .password("test")
                .accountId("test")
                .provider(AuthProvider.CREDENTIALS)
                .build());
        var role = rbacService.findOrCreateRoleByName(roleName);
        rbacService.assignRoleToUser(user.getUser(), role);
        return user.getUser();
    }
}
