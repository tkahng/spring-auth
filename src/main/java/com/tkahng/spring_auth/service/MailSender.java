package com.tkahng.spring_auth.service;


import com.tkahng.spring_auth.dto.EmailDto;

public interface MailSender {
    void sendMail(EmailDto notificationEmail) throws RuntimeException;
}