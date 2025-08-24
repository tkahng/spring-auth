package com.tkahng.spring_auth.rbac;

import java.util.List;
import java.util.UUID;

public interface RoleWithPermissions {
    UUID getId();

    String getName();

    String getDescription();

    List<PermissionView> getPermissions();

    interface PermissionView {
        UUID getId();

        String getName();

        String getDescription();
    }
}
