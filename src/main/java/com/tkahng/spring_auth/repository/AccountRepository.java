package com.tkahng.spring_auth.repository;


import com.tkahng.spring_auth.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {
    Optional<Account> findByUserIdAndProviderId(String userId, String providerId);
}
