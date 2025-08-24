package com.tkahng.spring_auth.mail;

import com.tkahng.spring_auth.users.User;

public interface MailService {
    void sendVerificationMail(User user);

    void sendResetPasswordMail(User user);
}
