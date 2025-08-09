package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.TestDataUtil;
import com.tkahng.spring_auth.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/db_test",
        "spring.jpa.hibernate.ddl-auto=none"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {
    private final UserRepository underTest;

    @Autowired
    public UserRepositoryTests(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalled() {
        User user = new User();
        user.setName("name1");
        user.setEmail("email1");
        underTest.save(user);
        Optional<User> result = underTest.findById(user.getId());
        assertThat(result).isPresent();
        var resultUser = result.get();
        assertThat(resultUser).isEqualTo(user);
    }

    @Test
    public void testThatMultipleUsersCanBeCreatedAndRecalled() {
        User userA = TestDataUtil.createTestAuthor();
        userA.setEmail("emaila");
        underTest.save(userA);
        User useB = TestDataUtil.createTestAuthor();
        useB.setEmail("emailb");
        underTest.save(useB);
        Iterable<User> result = underTest.findAll();
        assertThat(result)
                .hasSize(2)
                .containsExactly(userA, useB);
    }
}
