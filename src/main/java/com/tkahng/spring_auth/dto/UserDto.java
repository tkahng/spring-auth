package com.tkahng.spring_auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String email;
}
