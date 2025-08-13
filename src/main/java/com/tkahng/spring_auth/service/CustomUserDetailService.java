package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.CustomUserDetail;
import com.tkahng.spring_auth.dto.AuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final AuthService authService;

    public CustomUserDetailService(AuthService userAccountService) {
        this.authService = userAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userDetails = authService.findUserAccountByEmailAndProviderId(username,
                AuthProvider.CREDENTIALS.toString());
        if (userDetails == null || userDetails.getAccount() == null) {
            throw new UsernameNotFoundException("User not found");
        }
        var roles = authService.getRoleNamesByUserId(userDetails.getUser()
                .getId());
        var permissions = authService.getPermissionNamesByUserId(userDetails.getUser()
                .getId());
        log.info("roles {}", roles);
        return new CustomUserDetail(userDetails.getUser(), userDetails.getAccount(), roles, permissions);
    }
}
