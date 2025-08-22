package com.tkahng.spring_auth.security.oauth2;

import com.tkahng.spring_auth.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.tkahng.spring_auth.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(null);
        if (targetUrl != null) {
            String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", exception.getMessage())
                    .build()
                    .toUriString();
            httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
            response.sendRedirect(redirectUrl);
        }
        if (targetUrl == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"error\":\"oauth2_login_failed\",\"message\":\"" +
                            exception.getMessage()
                                    .replace("\"", "'") + "\"}");
        }

    }
}