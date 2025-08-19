package com.tkahng.spring_auth.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Slf4j
public class CustomUserDetail implements UserDetails, CredentialsContainer {
    private final List<String> roles;
    private final List<String> permissions;
    private final User user;
    private Account account;

    // UserDetails implementation...
    public CustomUserDetail(User user, Account account, List<String> roles, List<String> permissions) {
        this.user = user;
        this.account = account;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public void eraseCredentials() {
        this.account = null; // Securely dereference the password field
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : this.roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        log.info("authorities {}", authorities);
        return authorities;
    }

    @Override
    public String getPassword() {
        if (this.account == null) {
            return null;
        }
        return this.account.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }
}