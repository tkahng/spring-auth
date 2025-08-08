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
        user.setName("name1");
        user.setEmail("email1");
        user.setAccounts(new java.util.ArrayList<>());
        underTest.save(user);
        Optional<User> result = underTest.findById(user.getId());
        assertThat(result).isPresent();
        var resultUser = result.get();
        resultUser.setAccounts(new java.util.ArrayList<>());
        assertThat(resultUser).isEqualTo(user);
    }

    @Test
    public void testThatMultipleUsersCanBeCreatedAndRecalled() {
        User userA = TestDataUtil.createTestAuthor();
        userA.setEmail("emaila");
        userA.setAccounts(new java.util.ArrayList<>());
        underTest.save(userA);
        User useB = TestDataUtil.createTestAuthor();
        useB.setEmail("emailb");
        useB.setAccounts(new java.util.ArrayList<>());
        underTest.save(useB);
        Iterable<User> result = underTest.findAll();
         result.forEach(user -> user.setAccounts(new java.util.ArrayList<>()));
        assertThat(result)
                .hasSize(2)
                .containsExactly(userA, useB);
    }
}
