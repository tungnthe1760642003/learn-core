package org.example.learncore.service;

import lombok.RequiredArgsConstructor;
import org.example.learncore.model.User;
import org.example.learncore.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // Extract attributes from Google profile
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String googleId = (String) attributes.get("sub");

        // Sync user to database
        userRepository.findByEmail(email)
                .map(user -> {
                    user.setName(name);
                    user.setPicture(picture);
                    user.setGoogleId(googleId);
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .picture(picture)
                            .googleId(googleId)
                            .role("ROLE_USER")
                            .build();
                    return userRepository.save(newUser);
                });

        return oAuth2User;
    }
}
