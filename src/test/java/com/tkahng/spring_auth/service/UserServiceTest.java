package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    public void testThatUserCanBeCreatedAndRecalled() {
        AuthDto dto = new AuthDto();
        dto.setName("name1");
        dto.setEmail("email1");
        var newUser = userService.createUser(dto);
        Optional<User> result = userService.findUserByEmail(dto.getEmail());
        assertThat(result).isPresent();
        var resultUser = result.get();
        assertThat(resultUser).isEqualTo(newUser);
    }

    @Test
    @Rollback
    public void testThatMultipleUsersCanBeCreatedAndRecalled() {
        User userA = userService.createUser(new AuthDto().setEmail("emaila")
                .setName("namea"));
        User useB = userService.createUser(new AuthDto().setEmail("emailb")
                .setName("nameb"));
        Iterable<User> result = userRepository.findAll();
        assertThat(result)
                .hasSize(2)
                .containsExactly(userA, useB);
    }

    @Test
    @Rollback
    public void testUpdateUserEmailVerifiedAt() {
        User userA = userService.createUser(new AuthDto().setEmail("test+01@example.com")
        );
        User userB = userService.createUser(new AuthDto().setEmail("test+02@example.com")
        );
        assertThat(userA.getEmailVerifiedAt()).isNull();
        assertThat(userB.getEmailVerifiedAt()).isNull();
        userService.updateUserEmailVerifiedAt(userA.getId(), OffsetDateTime.now());
        var userA2 = userService.findUserByEmail(userA.getEmail())
                .orElseThrow();
        var userB2 = userService.findUserByEmail(userB.getEmail())
                .orElseThrow();
        assertThat(userA2.getEmailVerifiedAt()).isNotNull();
        assertThat(userB2.getEmailVerifiedAt()).isNull();
    }
    
}