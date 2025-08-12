package com.tkahng.spring_auth.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionId implements Serializable {
    private UUID roleId;
    private UUID permissionId;

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