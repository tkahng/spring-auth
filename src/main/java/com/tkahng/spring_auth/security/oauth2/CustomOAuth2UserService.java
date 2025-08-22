//package com.tkahng.spring_auth.security.oauth2;
//
//import com.tkahng.spring_auth.dto.AuthDto;
//import com.tkahng.spring_auth.dto.AuthProvider;
//import com.tkahng.spring_auth.security.oauth2.user.OAuth2UserInfo;
//import com.tkahng.spring_auth.security.oauth2.user.OAuth2UserInfoFactory;
//import com.tkahng.spring_auth.service.AuthService;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final AuthService authService;
//
//    public CustomOAuth2UserService(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration()
//                .getProviderDetails();
//        String userNameAttributeName = providerDetails.getUserInfoEndpoint()
//                .getUserNameAttributeName();
//        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//        AuthProvider provider = AuthProvider.fromString(userRequest.getClientRegistration()
//                .getRegistrationId());
//        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
//                provider, oAuth2User.getAttributes()
//        );
//        if (oAuth2UserInfo.getEmail() == null) {
//            throw new OAuth2AuthenticationException("email not found from OAuth2 provider");
//        }
//        var authDto = new AuthDto()
//                .setImage(oAuth2UserInfo.getImageUrl())
//                .setProvider(provider)
//                .setAccountId(oAuth2UserInfo.getId())
//                .setName(oAuth2UserInfo.getName())
//                .setEmail(oAuth2UserInfo.getEmail());
//
//
//        // You can wrap this in a custom `UserDetails` object if needed
//        return oAuth2User;
//    }
//}
