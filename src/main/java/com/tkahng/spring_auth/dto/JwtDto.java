package com.tkahng.spring_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
@Builder
public class JwtDto {
    @Email
    @NotEmpty
    private String email;
    private List<String> roles;
    private List<String> permissions;
}
