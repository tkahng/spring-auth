package com.tkahng.spring_auth;

import com.tkahng.spring_auth.dto.EmailDto;
import com.tkahng.spring_auth.service.MailSender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
public class MailSenderStub implements MailSender {

    @Getter
    private final List<EmailDto> sentEmails = new ArrayList<>();

    @Override
    public void sendMail(EmailDto notificationEmail) {
        // store email
        sentEmails.add(notificationEmail);

        // log nicely
        String logMessage = """
                [MailServiceStub] Sending email
                To: %s
                Subject: %s
                Body: %s
                """.formatted(
                notificationEmail.getRecipient(),
                notificationEmail.getSubject(),
                notificationEmail.getBody()
        );
        log.info(logMessage);
    }

    /**
     * helper to find emails sent to a specific recipient
     */
    public List<EmailDto> getEmailsTo(String recipient) {
        return sentEmails.stream()
                .filter(email -> email.getRecipient()
                        .equals(recipient))
                .collect(Collectors.toList());
    }

    /**
     * helper to clear stored emails between tests
     */
    public void clear() {
        sentEmails.clear();
    }
}
