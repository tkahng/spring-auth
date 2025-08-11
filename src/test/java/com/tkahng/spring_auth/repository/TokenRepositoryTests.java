package com.tkahng.spring_auth.repository;

import com.tkahng.spring_auth.domain.Token;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = true)
@EnableJpaAuditing
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TokenRepositoryTests {
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenRepositoryTests(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Test
    public void testThatTokenExpiredCanBeCreatedAndNotRetrieved() {
        var token = Token.builder()
                .identifier("identifier2")
                .expires(OffsetDateTime.now()
                        .minusSeconds(1)
                )
                .value("value2")
                .build();
        var savedToken = tokenRepository.save(token);
        var retrievedToken = tokenRepository.findByValueAndExpiresAfter("value2", OffsetDateTime.now())
                .orElse(null);
        assertThat(retrievedToken).isNull();
    }

    @Test
    public void testThatTokenNotExpiredCanBeCreatedAndRetrieved() {
        var token = Token.builder()
                .identifier("identifier")
                .expires(OffsetDateTime.now()
                        .plusDays(7))
                .value("value")
                .build();
        var savedToken = tokenRepository.save(token);
        var retrievedToken = tokenRepository.findByValueAndExpiresAfter("value", OffsetDateTime.now())
                .orElse(null);
        assertThat(retrievedToken).isNotNull();
    }

    @Test
    public void testThatTokenCanBeDeletedByValue() {
        var token = Token.builder()
                .identifier("identifier3")
                .expires(OffsetDateTime.now()
                        .plusDays(7))
                .value("value3")
                .build();
        tokenRepository.save(token);
        var retrievedToken = tokenRepository.findByValueAndExpiresAfter("value3", OffsetDateTime.now());
        assertThat(retrievedToken).isPresent();
        tokenRepository.deleteById(token.getId());
        var retrievedTokenAgain = tokenRepository.findByValueAndExpiresAfter("value3", OffsetDateTime.now());
        assertThat(retrievedTokenAgain).isEmpty();
    }
}
