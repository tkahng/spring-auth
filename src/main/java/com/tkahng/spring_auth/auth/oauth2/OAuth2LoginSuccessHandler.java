package com.tkahng.spring_auth.auth.oauth2;
// src/main/java/com/example/security/oauth/OAuth2LoginSuccessHandler.java

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkahng.spring_auth.auth.AuthService;
import com.tkahng.spring_auth.auth.dto.AuthDto;
import com.tkahng.spring_auth.auth.dto.AuthProvider;
import com.tkahng.spring_auth.auth.dto.AuthenticationResponse;
import com.tkahng.spring_auth.auth.oauth2.user.OAuth2UserInfoFactory;
import com.tkahng.spring_auth.common.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.OffsetDateTime;

import static com.tkahng.spring_auth.auth.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final AuthService authService;
    private final HttpCookieOAuth2AuthorizationRequestRepository requestRepository;
    private final ObjectMapper objectMapper;


    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        if (!(authentication instanceof OAuth2AuthenticationToken auth)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported authentication");
            return;
        }

        String registrationId = auth.getAuthorizedClientRegistrationId();
        AuthProvider provider = AuthProvider.fromString(registrationId);
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(registrationId, auth.getName());
        if (client == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No authorized client");
            return;
        }
        var userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                provider, auth.getPrincipal()
                        .getAttributes()
        );
        var accessToken = client.getAccessToken()
                .getTokenValue();
        var refreshToken = client.getRefreshToken() != null ? client.getRefreshToken()
                .getTokenValue() : null;
        var dto = new AuthDto()
                .setImage(userInfo.getImageUrl())
                .setProvider(provider)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setAccountId(userInfo.getId())
                .setIdToken(userInfo.getIdToken())
                .setEmail(userInfo.getEmail())
                .setName(userInfo.getName())
                .setEmailVerifiedAt(OffsetDateTime.now());
        AuthenticationResponse res;
        try {
            res = authService.oauth2Login(dto);
            respond(request, response, res);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void respond(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationResponse res
    ) throws IOException {
        var targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(null);
        if (targetUrl != null) {
            String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("access_token", res.getAccessToken())
                    .queryParam("refresh_token", res.getRefreshToken())
                    .build()
                    .toUriString();
            requestRepository.removeAuthorizationRequestCookies(request, response);
            response.sendRedirect(redirectUrl);
        } else {
            // 3. Configure the HttpServletResponse
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            // 4. Serialize the DTO to JSON and write it to the response
            try {
                String jsonResponse = objectMapper.writeValueAsString(res);
                response.getWriter()
                        .write(jsonResponse);
                response.getWriter()
                        .flush();
            } catch (IOException e) {
                // Handle exceptions, perhaps log it
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter()
                        .write("{\"error\": \"Error processing the request.\"}");
                response.getWriter()
                        .flush();
            }
        }
    }
}
