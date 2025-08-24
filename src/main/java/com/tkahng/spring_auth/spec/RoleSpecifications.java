package com.tkahng.spring_auth.spec;

import com.tkahng.spring_auth.dto.RoleFilter;
import com.tkahng.spring_auth.rbac.Role;
import com.tkahng.spring_auth.rbac.UserRole;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleSpecifications {

    public static Specification<Role> buildSpec(RoleFilter filter) {
        List<Specification<Role>> specs = new ArrayList<>();

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
        if (filter.getUserId() != null) {
            specs.add(belongsToUser(filter.getUserId()));
        }

        return Specification.allOf(specs); // new recommended way
    }

    private static Specification<Role> hasIds(List<UUID> ids) {
        return (root, query, cb) -> root.get("id")
                .in(ids);
    }

    private static Specification<Role> hasName(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Role> hasDescription(String description) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<Role> belongsToUser(UUID userId) {
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<UserRole> rp = subquery.from(UserRole.class);
            subquery.select(rp.get("id")
                            .get("roleId"))
                    .where(cb.equal(
                            rp.get("id")
                                    .get("userId"), userId
                    ));
            return root.get("id")
                    .in(subquery);
        };
    }
}