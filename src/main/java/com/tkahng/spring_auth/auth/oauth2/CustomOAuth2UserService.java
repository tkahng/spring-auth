// src/main/java/com/example/security/oauth/CustomOAuth2UserService.java
package com.tkahng.spring_auth.auth.oauth2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Load default user info first
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();
        Map<String, Object> attributes = new LinkedHashMap<>(oAuth2User.getAttributes());

        if ("github".equalsIgnoreCase(registrationId)) {
            if (attributes.get("email") == null) {
                // GitHub: fetch /user/emails for primary, verified email
                String email = fetchGitHubPrimaryEmail(userRequest.getAccessToken()
                        .getTokenValue());
                if (email != null) {
                    attributes.put("email", email);
                }
            }
        }

        // Return enriched user
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                userRequest.getClientRegistration()
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName()
        );
    }

    private String fetchGitHubPrimaryEmail(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            if (response.getStatusCode()
                    .is2xxSuccessful() && response.getBody() != null) {
                for (Object obj : response.getBody()) {
                    Map emailObj = (Map) obj;
                    Boolean primary = (Boolean) emailObj.get("primary");
                    Boolean verified = (Boolean) emailObj.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return emailObj.get("email")
                                .toString();
                    }
                }
            }
        } catch (Exception e) {
            // log and continue
            System.out.println("Failed to fetch GitHub email: " + e.getMessage());
        }
        return null;
    }
}
