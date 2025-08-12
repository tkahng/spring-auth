package com.tkahng.spring_auth.spec;

import com.tkahng.spring_auth.domain.Permission;
import com.tkahng.spring_auth.domain.RolePermission;
import com.tkahng.spring_auth.dto.PermissionFilter;
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
                    .where(cb.equal(rp.get("id")
                            .get("roleId"), roleId));
            return root.get("id")
                    .in(subquery);
        };
    }
}