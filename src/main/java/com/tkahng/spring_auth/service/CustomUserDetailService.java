package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.CustomUserDetail;
import com.tkahng.spring_auth.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final AuthService authService;
    public CustomUserDetailService(AuthService authService) {
        this.authService = authService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = authService.findUserByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        var userDetails = user.get();
        Optional<Account> account = authService.findAccountByUserIdAndProviderId(userDetails.getId(), "email");
        if (account.isEmpty()) {
            throw new UsernameNotFoundException("Credentials account not found");
        }
        var accountDetails = account.get();
        return new CustomUserDetail(userDetails.getEmail(), accountDetails.getPassword());
    }
}
