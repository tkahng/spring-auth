package com.tkahng.spring_auth.domain;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class CustomUserDetail  implements UserDetails, CredentialsContainer {
    private final String username;
    private String password;

    // UserDetails implementation...
    public CustomUserDetail(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void eraseCredentials() {
        this.password = null; // Securely dereference the password field
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}