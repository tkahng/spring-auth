package com.tkahng.spring_auth.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@EnableJpaAuditing
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {
    private final UserRepository underTest;

    @Autowired
    public UserRepositoryTests(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    @Rollback
    public void testThatUserCanBeCreatedAndRecalled() {
        User user = new User();
        user.setName("name1");
        user.setEmail("email1");
        underTest.saveAndFlush(user);
        Optional<User> result = underTest.findById(user.getId());
        assertThat(result).isPresent();
        var resultUser = result.get();
        assertThat(resultUser).isEqualTo(user);
    }

    @Test
    @Rollback
    public void testThatMultipleUsersCanBeCreatedAndRecalled() {
        User userA = User.builder()
                .name("namea")
                .email("emaila")
                .build();
        underTest.saveAndFlush(userA);
        User useB = User.builder()
                .name("nameb")
                .email("emailb")
                .build();
        underTest.saveAndFlush(useB);
        Iterable<User> result = underTest.findAll();
        assertThat(result)
                .hasSize(2)
                .containsExactly(userA, useB);
    }
}
