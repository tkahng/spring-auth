package com.tkahng.spring_auth.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkahng.spring_auth.auth.dto.AuthDto;
import com.tkahng.spring_auth.auth.dto.AuthProvider;
import com.tkahng.spring_auth.auth.dto.AuthenticationResponse;
import com.tkahng.spring_auth.jwt.JwtService;
import com.tkahng.spring_auth.rbac.RbacService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
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
        createUserWithRoleAndPermission("basic");
        testProtectedEndpoint("basic", "pro", true);
        testProtectedEndpoint("basic", "basic", false);
    }

    @Test
    @Rollback
    public void testProRole() throws Exception {
        createUserWithRoleAndPermission("pro");
        testProtectedEndpoint("pro", "basic", false);
        testProtectedEndpoint("pro", "pro", false);
        testProtectedEndpoint("pro", "advanced", true);
    }

    @Test
    @Rollback
    public void testAdvancedRole() throws Exception {
        createUserWithRoleAndPermission("advanced");
        testProtectedEndpoint("advanced", "basic", false);
        testProtectedEndpoint("advanced", "pro", false);
        testProtectedEndpoint("advanced", "advanced", false);

    }

    @Test
    @Rollback
    public void testAdminRole() throws Exception {
        createUserWithRoleAndPermission("admin");
        testProtectedEndpoint("admin", "basic", false);
        testProtectedEndpoint("admin", "pro", false);
        testProtectedEndpoint("admin", "advanced", false);
        testProtectedEndpoint("admin", "admin", false);
    }

    private void testProtectedEndpoint(String scope, String endPoint, boolean fail) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "email": "test@example.com", "password": "Password123!" }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationResponse authResponse =
                objectMapper.readValue(
                        loginResult.getResponse()
                                .getContentAsString(),
                        AuthenticationResponse.class
                );

        String accessToken = authResponse.getAccessToken();
        mockMvc.perform(get("/api/protected/" + endPoint)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(fail ? status().isForbidden() : status().isOk());
    }

    private void createUserWithRoleAndPermission(String roleName) {
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
    }
}
