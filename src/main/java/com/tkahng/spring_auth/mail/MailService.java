package com.tkahng.spring_auth.mail;

import com.tkahng.spring_auth.user.User;

public interface MailService {
    void sendVerificationMail(User user);

    void sendResetPasswordMail(User user);
}
