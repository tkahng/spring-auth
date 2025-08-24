package com.tkahng.spring_auth.mail.email;

import com.tkahng.spring_auth.dto.EmailDto;
import org.quartz.SchedulerException;

public interface MailQueue {
    void enqueueMail(EmailDto dto) throws SchedulerException;
}
