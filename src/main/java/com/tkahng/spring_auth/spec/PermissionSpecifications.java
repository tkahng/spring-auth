package com.tkahng.spring_auth.spec;

import com.tkahng.spring_auth.dto.PermissionFilter;
import com.tkahng.spring_auth.rbac.Permission;
import com.tkahng.spring_auth.rbac.RolePermission;
import com.tkahng.spring_auth.rbac.UserRole;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionSpecifications {

    public static Specification<Permission> buildSpec(PermissionFilter filter) {
        List<Specification<Permission>> specs = new ArrayList<>();

        if (filter.getIds() != null && !filter.getIds()
                .isEmpty()) {
            specs.add(hasIds(filter.getIds()));
        }
        if (filter.getName() != null && !filter.getName()
                .isBlank()) {
            specs.add(hasName(filter.getName()));
        }
        if (filter.getDescription() != null && !filter.getDescription()
                .isBlank()) {
            specs.add(hasDescription(filter.getDescription()));
        }
        if (filter.getRoleId() != null) {
            specs.add(belongsToRole(filter.getRoleId()));
        }
        if (filter.getUserId() != null) {
            specs.add(belongsToUser2(filter.getUserId()));
        }
        return Specification.allOf(specs); // new recommended way
    }

    private static Specification<Permission> hasIds(List<UUID> ids) {
        return (root, query, cb) -> root.get("id")
                .in(ids);
    }

    private static Specification<Permission> hasName(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Permission> hasDescription(String description) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<Permission> belongsToRole(UUID roleId) {
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<RolePermission> rp = subquery.from(RolePermission.class);
            subquery.select(rp.get("id")
                            .get("permissionId"))
                    .where(cb.equal(
                            rp.get("id")
                                    .get("roleId"), roleId
                    ));
            return root.get("id")
                    .in(subquery);
        };
    }

    public static Specification<Permission> belongsToUser2(UUID userId) {
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<RolePermission> rpRoot = subquery.from(RolePermission.class);
            Root<UserRole> urRoot = subquery.from(UserRole.class);

            subquery.select(rpRoot.get("id")
                            .get("permissionId"))
                    .distinct(true)
                    .where(
                            cb.equal(
                                    urRoot.get("id")
                                            .get("userId"), userId
                            ),
                            cb.equal(
                                    urRoot.get("id")
                                            .get("roleId"), rpRoot.get("id")
                                            .get("roleId")
                            )
                    );

            return root.get("id")
                    .in(subquery);
        };
    }

    public static Specification<Permission> belongsToUser(UUID userId) {
        return (root, query, cb) -> {
            query.distinct(true); // ensure uniqueness in SQL result

            // Subquery to get role IDs for the user
            Subquery<UUID> rolesSubquery = query.subquery(UUID.class);
            Root<UserRole> ur = rolesSubquery.from(UserRole.class);
            rolesSubquery.select(ur.get("id")
                            .get("roleId"))
                    .where(cb.equal(
                            ur.get("id")
                                    .get("userId"), userId
                    ));

            // Subquery to get permission IDs from those roles
            Subquery<UUID> permsSubquery = query.subquery(UUID.class);
            Root<RolePermission> rp = permsSubquery.from(RolePermission.class);
            permsSubquery.select(rp.get("id")
                            .get("permissionId"))
                    .where(rp.get("id")
                            .get("roleId")
                            .in(rolesSubquery));

            // Main query: permission.id in (permissions for roles for this user)
            return root.get("id")
                    .in(permsSubquery);
        };
    }
}