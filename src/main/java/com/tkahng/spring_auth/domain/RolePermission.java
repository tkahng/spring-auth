package com.tkahng.spring_auth.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "role_permission")
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId") // maps PK field to this relation's ID
    @JoinColumn(name = "permission_id")
    private Permission permission;
}