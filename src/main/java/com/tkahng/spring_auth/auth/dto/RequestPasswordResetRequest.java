package com.tkahng.spring_auth.auth.dto;

import lombok.Data;

@Data
public class RequestPasswordResetRequest {
    private String email;
}
