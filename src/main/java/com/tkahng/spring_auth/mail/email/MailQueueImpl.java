package com.tkahng.spring_auth.mail.email;

import com.tkahng.spring_auth.mail.EmailDto;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailQueueImpl implements MailQueue {
    private final Scheduler scheduler;

    @Override
    public void enqueueMail(EmailDto dto) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("recipient", dto.getRecipient());
        jobDataMap.put("subject", dto.getSubject());
        jobDataMap.put("body", dto.getBody());

        JobDetail jobDetail = JobBuilder.newJob(SendMailJob.class)
                .withIdentity(
                        UUID.randomUUID()
                                .toString(), "mail-jobs"
                )
                .setJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
