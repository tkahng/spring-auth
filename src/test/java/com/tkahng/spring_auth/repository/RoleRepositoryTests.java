package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.rbac.Role;
import com.tkahng.spring_auth.rbac.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest(showSql = true)
@EnableJpaAuditing
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository roleRepository;


    private Role createRole(String name) {
        return roleRepository.saveAndFlush(Role.builder()
                .name(name)
                .build());
    }

    private ArrayList<Role> createNumberedRoles(int number) {
        var roles = new ArrayList<Role>();
        for (int i = 0; i < number; i++) {
            var role = createRole("role" + i);
            roles.add(role);
        }
        return roles;
    }

    @Test
    @Rollback
    public void testPaginateByName() {
        var roles = createNumberedRoles(3);
        var result = roleRepository.findAll(
                (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + "role".toLowerCase() + "%"),
                Pageable.unpaged()
        );
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);

    }
}
