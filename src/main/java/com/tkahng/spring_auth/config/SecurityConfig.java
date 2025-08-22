package com.tkahng.spring_auth.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.tkahng.spring_auth.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.tkahng.spring_auth.security.oauth2.OAuth2LoginFailureHandler;
import com.tkahng.spring_auth.security.oauth2.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize, @PostAuthorize, @Secured, etc.
public class SecurityConfig {
    @Autowired
    private OAuth2LoginSuccessHandler successHandler;

    @Autowired
    private OAuth2LoginFailureHandler failureHandler;

    @Value("${jwt.key}")
    private String jwtKey;

    /**
     * this allows us to use templating in security expressions.
     * e.g. @PreAuthorize("hasRole(#role)")
     */
    @Bean
    static AnnotationTemplateExpressionDefaults templateExpressionDefaults() {
        return new AnnotationTemplateExpressionDefaults();
    }

    // and, if using pre-post method security also add
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        //expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> accessTokenResponseClient() {
        return new RestClientRefreshTokenTokenResponseClient();
    }

    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder()
                    .decode(jwtKey);
        } catch (IllegalArgumentException ex) {
            keyBytes = jwtKey.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "HMAC key must be at least 256 bits (32 bytes). Current length: " + keyBytes.length + " bytes"
            );
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey.getEncoded()));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .anyRequest()
                                .permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt((jwt) ->
                                jwt
                                        .decoder(jwtDecoder(secretKey()))
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .oauth2Login(auth -> auth.authorizationEndpoint(
                                ae -> ae.authorizationRequestRepository(cookieAuthorizationRequestRepository()))
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                );

        return http.build();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // 👇 Set the claim name that contains your roles in the JWT
        // For example, if your JWT has { "roles": ["basic", "admin"] }
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtConverter;
    }
}