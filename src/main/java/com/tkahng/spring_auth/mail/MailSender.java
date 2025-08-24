package com.tkahng.spring_auth.mail;


public interface MailSender {
    void sendMail(EmailDto notificationEmail) throws RuntimeException;
}