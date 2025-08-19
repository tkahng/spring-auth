package com.tkahng.spring_auth;

import com.tkahng.spring_auth.service.MailService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestMailConfig {
    @Bean
    public MailService mailService() {
        return new MailServiceStub();
    }
}