package com.tkahng.spring_auth.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
public class RolePermissionId implements Serializable {
    private final UUID roleId;
    private final UUID permissionId;

    private RolePermissionId(UUID permissionId, UUID roleId) {
        this.permissionId = permissionId;
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RolePermissionId pk = (RolePermissionId) o;
        return Objects.equals(getPermissionId(), pk.getPermissionId()) &&
                Objects.equals(getRoleId(), pk.getRoleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPermissionId(), getRoleId());
    }
}