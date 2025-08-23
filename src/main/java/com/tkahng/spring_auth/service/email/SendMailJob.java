package com.tkahng.spring_auth.service.email;

import com.tkahng.spring_auth.dto.EmailDto;
import com.tkahng.spring_auth.service.MailSender;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendMailJob implements Job {
    @Autowired
    private MailSender mailSender;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String recipientEmail = jobDataMap.getString("recipient");
        mailSender.sendMail(new EmailDto().setBody(body)
                .setSubject(subject)
                .setRecipient(recipientEmail));
    }
}