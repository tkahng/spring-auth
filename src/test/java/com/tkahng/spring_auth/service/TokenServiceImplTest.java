package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.token.TokenRepository;
import com.tkahng.spring_auth.token.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TokenServiceImplTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenService tokenService;

    @Test
    @Rollback
    @DisplayName("Delete token by identifier and type")
    void deleteByIdentifierAndType() {
        tokenService.createToken("id1", "type1", 8000);
        tokenService.createToken("id1", "type1", 8000);
        tokenService.createToken("id1", "type2", 8000);

        var count = tokenService.deleteByIdentifierAndType("id1", "type1");
        Assertions.assertThat(count)
                .isEqualTo(2);

        var remaining = tokenService.findByIdentifier("id1", Pageable.unpaged())
                .getContent()
                .size();
        Assertions.assertThat(remaining)
                .isEqualTo(1);
    }
}