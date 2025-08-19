package com.tkahng.spring_auth;

import com.tkahng.spring_auth.service.MailSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestMailConfig {
    @Bean
    public MailSender mailService() {
        return new MailSenderStub();
    }
}