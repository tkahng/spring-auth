package com.tkahng.spring_auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleDto {

    //    @NonNull
    @NotEmpty(message = "name is required")
    private String name;

    private String description;
}
