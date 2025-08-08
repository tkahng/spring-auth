package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.TestDataUtil;
import com.tkahng.spring_auth.domain.Account;
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
public class AccountRepositoryTests {
    private final UserRepository userRepository;
    private final AccountRepository underTest;

    @Autowired
    public AccountRepositoryTests(AccountRepository underTest, UserRepository userRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @Test
    public void testThatAccountCanBeCreatedAndRecalled() {
        User user = new User();
        user.setName("name");
        user.setEmail("email");
        user = userRepository.save(user);
        Account account = new Account();
        account.setUser(user);
        account.setAccountId("accountId");
        account.setProviderId("providerId");
        account = underTest.save(account);
        Optional<Account> result = underTest.findById(account.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(account);
    }

}
