package com.tkahng.spring_auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AuthDto {
    @NonNull
    private String email;
    private String password;
    private String name;
    private AuthProvider provider;
    private String accountId;
}