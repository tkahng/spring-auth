package com.tkahng.spring_auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.dto.AuthProvider;
import com.tkahng.spring_auth.dto.AuthenticationResponse;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void testBasicSuccess() throws Exception {
        testProtectedEndpoint("basic", "basic", false);
    }

    @Test
    @Rollback
    public void testProBasicSuccess() throws Exception {
        testProtectedEndpoint("pro", "basic", false);
    }

    @Test
    @Rollback
    public void testProSuccess() throws Exception {
        testProtectedEndpoint("pro", "pro", false);
    }

    @Test
    @Rollback
    public void testAdvancedBasicSuccess() throws Exception {
        testProtectedEndpoint("advanced", "basic", false);
    }

    @Test
    @Rollback
    public void testAdvancedProSuccess() throws Exception {
        testProtectedEndpoint("advanced", "pro", false);
    }

    @Test
    @Rollback
    public void testAdvancedSuccess() throws Exception {
        testProtectedEndpoint("advanced", "advanced", false);
    }

    @Test
    @Rollback
    public void testAdminBasicSuccess() throws Exception {
        testProtectedEndpoint("admin", "basic", false);
    }

    @Test
    @Rollback
    public void testAdminProSuccess() throws Exception {
        testProtectedEndpoint("admin", "pro", false);
    }

    @Test
    @Rollback
    public void testAdminAdvancedSuccess() throws Exception {
        testProtectedEndpoint("admin", "advanced", false);
    }

    @Test
    @Rollback
    public void testAdminSuccess() throws Exception {
        testProtectedEndpoint("admin", "admin", false);
    }

    @Test
    @Rollback
    public void testProWithBasicFail() throws Exception {
        testProtectedEndpoint("basic", "pro", true);
    }

    @Test
    @Rollback
    public void testAdvancedWithProFail() throws Exception {
        testProtectedEndpoint("pro", "advanced", true);
    }


    private void testProtectedEndpoint(String scope, String endPoint, boolean fail) throws Exception {
        var user = createUserWithRoleAndPermission(scope);
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "email": "test@example.com", "password": "Password123!" }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationResponse authResponse =
                objectMapper.readValue(loginResult.getResponse()
                                .getContentAsString(),
                        AuthenticationResponse.class);

        String accessToken = authResponse.getAccessToken();
        mockMvc.perform(get("/api/protected/" + endPoint)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(fail ? status().isForbidden() : status().isOk());
    }

    private User createUserWithRoleAndPermission(String roleName) {
        rbacService.initRolesAndPermissions();
        var user = authService.createUserAndAccount(AuthDto.builder()
                .email("test@example.com")
                .name("test")
                .password("Password123!")
                .accountId("test")
                .provider(AuthProvider.CREDENTIALS)
                .build());
        var role = rbacService.findOrCreateRoleByName(roleName);
        //var permission = rbacService.findOrCreatePermissionByName(roleName);
        //rbacService.assignPermissionToRole(role, permission);
        rbacService.assignRoleToUser(user.getUser(), role);
        return user.getUser();
    }
}
