package com.tkahng.spring_auth;

import com.tkahng.spring_auth.domain.Permission;
import com.tkahng.spring_auth.domain.Role;
import lombok.NonNull;

public class DataUtils {
    public static Role createRole(@NonNull String name, String description) {
        return Role.builder()
                .name(name)
                .description(description)
                .build();
    }

    public static Permission createPermission(@NonNull String name, String description) {
        return Permission.builder()
                .name(name)
                .description(description)
                .build();
    }
}
