package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.Account;
import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.AuthDto;
import com.tkahng.spring_auth.repository.AccountRepository;
import com.tkahng.spring_auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    @Rollback
    public void testThatAccountCanBeCreatedAndRecalled() {
        User user = userService.createUser(new AuthDto().setEmail("email1@email.com"));
        Account account = accountService.createAccount(Account.builder()
                .user(user)
                .accountId("accountId1")
                .providerId("providerId1")
                .build());

        Optional<Account> result = accountService.findById(account.getId());
        assertThat(result).isPresent();
        var resultAccount = result.get();
        assertThat(resultAccount).isEqualTo(account);
    }

    @Test
    @Rollback
    public void testThatMultipleAccountsCanBeCreatedAndRecalled() {
        User user = userService.createUser(new AuthDto().setEmail("email3@email.com"));
        Account accountA = accountService.createAccount(Account.builder()
                .user(user)
                .accountId("accountId2A")
                .providerId("providerId2A")
                .build());
        Account accountB = accountService.createAccount(Account.builder()
                .user(user)
                .accountId("accountId2B")
                .providerId("providerId2B")
                .build());

        Iterable<Account> result = accountRepository.findAll();
        assertThat(result)
                .hasSize(2)
                .containsExactly(accountA, accountB);
    }

    @Test
    @Rollback
    public void testThatAccountsWithSameProviderIdCannotBeCreated() {
        User user = userService.createUser(new AuthDto().setEmail("email4@email.com"));
        Account accountA = accountService.createAccount(Account.builder()
                .user(user)
                .accountId("accountId3A")
                .providerId("providerId3A")
                .build());

        //
        try {
            Account accountB = accountService.createAccount(Account.builder()
                    .user(user)
                    .accountId("accountId3B")
                    .providerId("providerId3A")
                    .build());
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("duplicate key value violates unique constraint");
        }
    }

    @Test
    @Rollback
    public void testThatAccountCanBeDeleted() {
        User user = userService.createUser(new AuthDto().setEmail("email5@email.com"));

        Account account = accountService.createAccount(Account.builder()
                .user(user)
                .accountId("accountId4")
                .providerId("providerId4")
                .build());

        accountService.deleteAccount(account);
        Optional<Account> result = accountService.findById(account.getId());
        assertThat(result).isEmpty();
    }

}