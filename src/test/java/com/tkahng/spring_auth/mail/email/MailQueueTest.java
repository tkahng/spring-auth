package com.tkahng.spring_auth.mail.email;

import com.tkahng.spring_auth.MailSenderStub;
import com.tkahng.spring_auth.mail.EmailDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MailQueueTest {

    @Autowired
    MailQueue mailQueue;
    @Autowired
    MailSenderStub mailSenderStub;

    @Test
    void jobIsEnqueuedAndRuns() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        // add a hook in stub to count down
        mailSenderStub.setLatch(latch);

        EmailDto dto = EmailDto.builder()
                .recipient("test@example.com")
                .subject("Hello")
                .body("World")
                .build();

        mailQueue.enqueueMail(dto);

        // wait for Quartz worker thread
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        assertThat(completed).isTrue();

        var emails = mailSenderStub.getEmailsTo("test@example.com");
        assertThat(emails.size()).isEqualTo(1);
        assertThat(emails.getFirst()
                .getSubject()).isEqualTo("Hello");
    }
}