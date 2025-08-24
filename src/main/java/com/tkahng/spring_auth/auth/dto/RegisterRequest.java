package com.tkahng.spring_auth.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Email
    @NotEmpty(message = "Email is required")
    private String email;

    private String name;
    @NotBlank(message = "Password is required")
    private String password;

}
