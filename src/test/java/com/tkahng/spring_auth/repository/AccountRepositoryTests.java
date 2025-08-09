package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.Account;
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

@DataJpaTest(showSql = true)
@EnableJpaAuditing
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/db_test",
        "spring.jpa.hibernate.ddl-auto=none"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        User user = User.builder()
                .name("name2")
                .email("email2")
                .build();

        userRepository.save(user);
        Account account = Account.builder()
                .user(user)
                .accountId("accountId1")
                .providerId("providerId1")
                .build();

        underTest.save(account);
        Optional<Account> result = underTest.findById(account.getId());
        assertThat(result).isPresent();
        var resultAccount = result.get();
        assertThat(resultAccount).isEqualTo(account);
    }

    @Test
    public void testThatMultipleAccountsCanBeCreatedAndRecalled() {
        User user = new User();
        user.setName("name3");
        user.setEmail("email3");
        userRepository.save(user);
        Account accountA = new Account();
        accountA.setUser(user);
        accountA.setAccountId("accountId2A");
        accountA.setProviderId("providerId2A");
        underTest.save(accountA);
        Account accountB = new Account();
        accountB.setUser(user);
        accountB.setAccountId("accountId2B");
        accountB.setProviderId("providerId2B");
        underTest.save(accountB);
        Iterable<Account> result = underTest.findAll();
        assertThat(result)
                .hasSize(2)
                .containsExactly(accountA, accountB);
    }

    @Test
    public void testThatAccountsWithSameProviderIdCannotBeCreated() {
        User user = User.builder()
                .email("email4")
                .name("name4")
                .build();

        userRepository.save(user);
        Account accountA = Account
                .builder()
                .user(user)
                .accountId("accountId3A")
                .providerId("providerId3A")
                .build();

        underTest.save(accountA);
        Account accountB = Account.builder()
                .user(user)
                .accountId("accountId3B")
                .providerId("providerId3A")
                .build();
//
        try {
            underTest.save(accountB);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Unique index or primary key violation");
        }
    }

    @Test
    public void testThatAccountCanBeDeleted() {
        User user = User.builder()
                .email("email5")
                .name("name5")
                .build();

        userRepository.save(user);
        Account account = new Account();
        account.setUser(user);
        account.setAccountId("accountId4");
        account.setProviderId("providerId4");
        underTest.save(account);
        underTest.delete(account);
        Optional<Account> result = underTest.findById(account.getId());
        assertThat(result).isEmpty();
    }


}
