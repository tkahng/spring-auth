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
        user.setName("name2");
        user.setEmail("email2");
        userRepository.save(user);
        User managedUser = userRepository.findById(user.getId()).orElseThrow();
        Account account = new Account();
        account.setUser(managedUser);
        account.setAccountId("accountId1");
        account.setProviderId("providerId1");
        underTest.save(account);
        Optional<Account> result = underTest.findById(account.getId());
        assertThat(result).isPresent();
        var resultAccount = result.get();
        resultAccount.setUser(managedUser);
        assertThat(resultAccount).isEqualTo(account);
    }

    @Test
    public void testThatMultipleAccountsCanBeCreatedAndRecalled() {
        User user = TestDataUtil.createTestAuthor();
        user.setName("name3");
        user.setEmail("email3");
        userRepository.save(user);
        User managedUser = userRepository.findById(user.getId()).orElseThrow();
        Account accountA = new Account();
        accountA.setUser(managedUser);
        accountA.setAccountId("accountId2A");
        accountA.setProviderId("providerId2A");
        underTest.save(accountA);
        Account accountB = new Account();
        accountB.setUser(managedUser);
        accountB.setAccountId("accountId2B");
        accountB.setProviderId("providerId2B");
        underTest.save(accountB);
        Iterable<Account> result = underTest.findAll();
        result.forEach(account -> account.setUser(managedUser));
        assertThat(result)
                .hasSize(2)
                .containsExactly(accountA, accountB);
    }

    @Test
    public void testThatAccountsWithSameProviderIdCannotBeCreated(){
        User user = TestDataUtil.createTestAuthor();
        user.setName("name4");
        user.setEmail("email4");
        userRepository.save(user);
        User managedUser = userRepository.findById(user.getId()).orElseThrow();
        Account accountA = new Account();
        accountA.setUser(managedUser);
        accountA.setAccountId("accountId3A");
        accountA.setProviderId("providerId3A");
        underTest.save(accountA);
        Account accountB = new Account();
        accountB.setUser(managedUser);
        accountB.setAccountId("accountId3B");
        accountB.setProviderId("providerId3A");
        try{
            underTest.save(accountB);
        } catch (Exception e){
            assertThat(e.getMessage()).contains("Unique index or primary key violation");
        }

    }
}
