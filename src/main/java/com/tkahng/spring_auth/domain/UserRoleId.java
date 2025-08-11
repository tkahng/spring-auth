package com.tkahng.spring_auth.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
public class UserRoleId implements Serializable {


    private final UUID userId;
    private final UUID roleId;


    private UserRoleId(UUID userId, UUID roleId) {
        this.userId = userId;
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
        UserRoleId pk = (UserRoleId) o;
        return Objects.equals(getUserId(), pk.getUserId()) &&
                Objects.equals(getRoleId(), pk.getRoleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getRoleId());
    }
}