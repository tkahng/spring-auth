package com.tkahng.spring_auth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetPasswordRequest {
    private String password;
    private String confirmPassword;
}
