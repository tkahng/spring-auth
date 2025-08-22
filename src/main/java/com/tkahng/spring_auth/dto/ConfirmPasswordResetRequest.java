package com.tkahng.spring_auth.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ConfirmPasswordResetRequest {
    private String password;
    private String confirmPassword;
}
