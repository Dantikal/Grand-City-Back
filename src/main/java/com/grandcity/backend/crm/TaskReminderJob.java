package com.grandcity.backend.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Once a minute, sends Telegram reminders for CRM tasks that are about to fall due. */
@Component
public class TaskReminderJob {

    private static final Logger log = LoggerFactory.getLogger(TaskReminderJob.class);

    private final CrmService crm;
    private final int minutesBefore;

    public TaskReminderJob(CrmService crm, @Value("${app.crm.remind-before-minutes}") int minutesBefore) {
        this.crm = crm;
        this.minutesBefore = minutesBefore;
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void run() {
        int sent = crm.sendDueReminders(minutesBefore);
        if (sent > 0) log.info("Sent {} CRM task reminder(s)", sent);
    }
}
