package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.*;
import com.tkahng.spring_auth.dto.CreatePermissionDto;
import com.tkahng.spring_auth.dto.CreateRoleDto;
import com.tkahng.spring_auth.dto.PermissionFilter;
import com.tkahng.spring_auth.dto.RoleFilter;
import com.tkahng.spring_auth.repository.PermissionRepository;
import com.tkahng.spring_auth.repository.RolePermissionRepository;
import com.tkahng.spring_auth.repository.RoleRepository;
import com.tkahng.spring_auth.repository.UserRoleRepository;
import com.tkahng.spring_auth.spec.PermissionSpecifications;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RbacServiceImpl implements RbacService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final Map<String, List<String>> rolePermissionMap;

    public RbacServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, UserRoleRepository userRoleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionMap = new java.util.HashMap<>();
        this.rolePermissionMap.put("basic", List.of("basic"));
        this.rolePermissionMap.put("pro", List.of("basic", "pro"));
        this.rolePermissionMap.put("advanced", List.of("basic", "pro", "advanced"));
        this.rolePermissionMap.put("admin", List.of("basic", "pro", "advanced", "admin"));
    }

    public static Specification<Permission> notAssignedToRole(UUID roleId) {
        return (root, query, cb) -> {
            // Subquery for role_permissions.permission_id
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<RolePermission> rp = subquery.from(RolePermission.class);
            subquery.select(rp.get("id")
                            .get("permissionId"))
                    .where(cb.equal(rp.get("id")
                            .get("roleId"), roleId));

            // p.id NOT IN (subquery)
            return cb.not(root.get("id")
                    .in(subquery));
        };
    }

    public static Specification<Permission> belongsToRole(UUID roleId) {
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

    @Override
    public Permission createPermission(@NonNull CreatePermissionDto createPermissionDto) {
        return permissionRepository.saveAndFlush(Permission.builder()
                .name(createPermissionDto.getName())
                .description(createPermissionDto.getDescription())
                .build());
    }

    @Override
    public Map<String, List<String>> getRolePermissionMap() {
        return rolePermissionMap;
    }

    @Override
    public Role createRole(@NonNull CreateRoleDto createRoleDto) {
        return roleRepository.saveAndFlush(Role.builder()
                .name(createRoleDto.getName())
                .description(createRoleDto.getDescription())
                .build());
    }

    @Override
    public void assignRoleToUser(@NotNull User user, @NotNull Role role) {
        UserRole userRole = new UserRole();
        userRole.setId(UserRoleId.builder()
                .userId(user.getId())
                .roleId(role.getId())
                .build());
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.saveAndFlush(userRole);
    }

    @Override
    public void assignPermissionToRole(Role role, Permission permission) {
        var existingRolePermission = rolePermissionRepository.findByRoleIdAndPermissionId(role.getId(), permission.getId());
        if (existingRolePermission.isPresent()) {
            return;
        }
        var rolePermission = RolePermission.builder()
                .id(
                        RolePermissionId.builder()
                                .roleId(role.getId())
                                .permissionId(permission.getId())
                                .build())
                .role(role)
                .permission(permission)
                .build();
        rolePermissionRepository.saveAndFlush(rolePermission);
    }

    @Override
    public Optional<Permission> findPermissionById(UUID id) {
        return permissionRepository.findById(id);
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Role findOrCreateRoleByName(String name) {
        var role = roleRepository.findByName(name)
                .orElse(null);
        if (role == null) {
            role = roleRepository.saveAndFlush(Role.builder()
                    .name(name)
                    .build());
        }
        return role;
    }

    private Specification<Role> filterRoles(RoleFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.getIds() != null && !filter.getIds()
                    .isEmpty()) {
                predicates.getExpressions()
                        .add(root.get("id")
                                .in(filter.getIds()));
            }

            if (filter.getName() != null && !filter.getName()
                    .isBlank()) {
                predicates.getExpressions()
                        .add(
                                cb.like(cb.lower(root.get("name")), "%" + filter.getName()
                                        .toLowerCase() + "%")
                        );
            }

            if (filter.getDescription() != null && !filter.getDescription()
                    .isBlank()) {
                predicates.getExpressions()
                        .add(
                                cb.like(cb.lower(root.get("description")), "%" + filter.getDescription()
                                        .toLowerCase() + "%")
                        );
            }

            return predicates;
        };
    }

    @Override
    public Page<Role> findAllRoles(RoleFilter filter, Pageable pageable) {
        return roleRepository.findAll(filterRoles(filter), pageable);
    }

    @Override
    public Optional<Role> findRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Permission> findPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    public Permission findOrCreatePermissionByName(String name) {
        var permission = permissionRepository.findByName(name)
                .orElse(null);
        if (permission == null) {
            permission = permissionRepository.saveAndFlush(Permission.builder()
                    .name(name)
                    .build());
        }
        return permission;
    }

    private Specification<Permission> filterPermission(PermissionFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.getIds() != null && !filter.getIds()
                    .isEmpty()) {
                predicates.getExpressions()
                        .add(root.get("id")
                                .in(filter.getIds()));
            }

            if (filter.getName() != null && !filter.getName()
                    .isBlank()) {
                predicates.getExpressions()
                        .add(
                                cb.like(cb.lower(root.get("name")), "%" + filter.getName()
                                        .toLowerCase() + "%")
                        );
            }

            if (filter.getDescription() != null && !filter.getDescription()
                    .isBlank()) {
                predicates.getExpressions()
                        .add(
                                cb.like(cb.lower(root.get("description")), "%" + filter.getDescription()
                                        .toLowerCase() + "%")
                        );
            }
            if (filter.getRoleId() != null) {
                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<RolePermission> rp = subquery.from(RolePermission.class);
                subquery.select(rp.get("id")
                                .get("permissionId"))
                        .where(cb.equal(rp.get("id")
                                .get("roleId"), filter.getRoleId()));


                predicates.getExpressions()
                        .add(
                                root.get("id")
                                        .in(subquery)
                        );
            }

            return predicates;
        };
    }

    @Override
    public Page<Permission> findAllPermissions(PermissionFilter filter, Pageable pageable) {
        return permissionRepository.findAll(PermissionSpecifications.buildSpec(filter), pageable);
    }

    @Override
    public void initRolesAndPermissions() {
        for (Map.Entry<String, List<String>> entry : this.rolePermissionMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            var role = findOrCreateRoleByName(key);

            for (String value : values) {
                var permission = findOrCreatePermissionByName(value);
                assignPermissionToRole(role, permission);
            }
        }
    }


}
