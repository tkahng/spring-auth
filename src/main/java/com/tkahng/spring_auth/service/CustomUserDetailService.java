package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.CustomUserDetail;
import com.tkahng.spring_auth.dto.AuthProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final AuthService userAccountService;

    public CustomUserDetailService(AuthService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userDetails = userAccountService.findUserAccountByEmailAndProviderId(username, AuthProvider.CREDENTIALS.toString());
        if (userDetails == null || userDetails.getAccount() == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetail(userDetails.getUser().getEmail(), userDetails.getAccount().getPassword());
    }
}
