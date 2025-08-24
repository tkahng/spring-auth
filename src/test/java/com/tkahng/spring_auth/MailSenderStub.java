package com.tkahng.spring_auth;

import com.tkahng.spring_auth.dto.EmailDto;
import com.tkahng.spring_auth.mail.MailSender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
public class MailSenderStub implements MailSender {


    @Getter
    private final List<EmailDto> sentEmails = new ArrayList<>();
    @Setter
    private volatile CountDownLatch latch;

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
        if (latch != null) {
            latch.countDown();
        }
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

    public void remove(EmailDto email) {
        sentEmails.remove(email);
    }

    public String getLinkParam(String html, String paramName) throws URISyntaxException {
        Pattern pattern = Pattern.compile("href\\s*=\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            throw new RuntimeException("No link found in HTML");
        }
        String url = matcher.group(1);

        // Step 2: Parse URI and extract token
        URI uri = new URI(url);
        String query = uri.getQuery(); // e.g. token=some+random+token+with+spaces%26symbols%21
        String token = null;
        String[] pair = query.split("=", 2);
        if (pair.length == 2 && pair[0].equals(paramName)) {
            token = pair[1];
        }
        return token;
    }
}
