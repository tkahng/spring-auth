package com.tkahng.spring_auth.mail;


import com.tkahng.spring_auth.dto.EmailDto;

public interface MailSender {
    void sendMail(EmailDto notificationEmail) throws RuntimeException;
}