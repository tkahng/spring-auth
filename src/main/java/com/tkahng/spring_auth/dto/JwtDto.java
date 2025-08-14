package com.tkahng.spring_auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Accessors(chain = true)
@Data
@Builder
public class JwtDto {
    @NonNull
    private UUID userId;
    @NotEmpty
    private String email;
    private OffsetDateTime emailVerifiedAt;
    private List<String> roles;
    private List<String> permissions;
}
