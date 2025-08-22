package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.User;

public interface MailService {
    void sendVerificationMail(User user);
}
