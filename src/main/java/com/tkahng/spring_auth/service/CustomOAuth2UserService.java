//package com.tkahng.spring_auth.service;
//
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final AuthService userRepository;
//
//    public CustomOAuth2UserService(AuthService userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//
//        String providerId = userRequest.getClientRegistration()
//                .getRegistrationId(); // "google"
//        String googleId = oAuth2User.getAttribute("sub"); // Google's unique ID
//        String email = oAuth2User.getAttribute("email");
//        String name = oAuth2User.getAttribute("name");
//        String picture = oAuth2User.getAttribute("picture");
//        // 🔹 Find or create user in your DB
//        User user = userRepository.findByEmail(email)
//                .orElseGet(() -> {
//                    User newUser = new User();
//                    newUser.setEmail(email);
//                    newUser.setName(name);
//                    newUser.setProvider("GOOGLE");
//                    return userRepository.save(newUser);
//                });
//
//        // You can wrap this in a custom `UserDetails` object if needed
//        return new CustomUserPrincipal(user, oAuth2User.getAttributes());
//    }
//}
