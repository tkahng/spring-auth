package com.tkahng.spring_auth.auth.oauth2.user;

import com.tkahng.spring_auth.auth.dto.AuthProvider;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider registrationId, Map<String, Object> attributes) {
        switch (registrationId) {
            case GOOGLE -> {
                return new GoogleOAuth2UserInfo(attributes);
            }
            case GITHUB -> {
                return new GithubOAuth2UserInfo(attributes);
            }
            default -> throw new OAuth2AuthenticationException(
                    "Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
