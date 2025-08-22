package com.tkahng.spring_auth.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Slf4j
public class CustomUserDetail implements OAuth2User, UserDetails {
    private final List<String> roles;
    private final List<String> permissions;
    private final User user;
    private final Account account;
    private final Map<String, Object> attributes;

    // UserDetails implementation...
    public CustomUserDetail(
            User user, Account account, List<String> roles, List<String> permissions, Map<String, Object> attributes) {
        this.user = user;
        this.account = account;
        this.roles = roles;
        this.permissions = permissions;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // add roles as ROLE_ prefixed authorities.
        for (String role : this.roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        // add permissions as custom authorities.
        for (String permission : this.permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
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

    @Override
    public String getName() {
        return this.user.getName();
    }
}