package com.tkahng.spring_auth.rbac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionFilter {
    private List<UUID> ids;
    private String name;
    private String description;
    private UUID roleId;
    private UUID userId;
}
