package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.TestDataUtil;
import com.tkahng.spring_auth.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTests {
    private final UserRepository underTest;

    @Autowired
    public UserRepositoryTests(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalled() {
        User user = new User();
        user.setName("name");
        user.setEmail("email");
        underTest.save(user);
        Optional<User> result = underTest.findById(user.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
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
