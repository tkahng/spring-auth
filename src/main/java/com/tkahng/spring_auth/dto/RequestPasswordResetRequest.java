package com.tkahng.spring_auth.dto;

import lombok.Data;

@Data
public class RequestPasswordResetRequest {
    private String email;
}
