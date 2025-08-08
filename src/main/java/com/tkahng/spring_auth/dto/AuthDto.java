package com.tkahng.spring_auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthDto {
    private String email;
    private String name;
    private String providerId;
    private String password;
    private String accountId;
}