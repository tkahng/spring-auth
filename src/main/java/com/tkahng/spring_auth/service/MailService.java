package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.users.User;

public interface MailService {
    void sendVerificationMail(User user);

    void sendResetPasswordMail(User user);
}
