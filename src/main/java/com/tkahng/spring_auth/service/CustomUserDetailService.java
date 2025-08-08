package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.CustomUserDetail;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        var userDetails = user.get();
        return new CustomUserDetail(userDetails.getEmail(), userDetails.getPassword());
    }
}
