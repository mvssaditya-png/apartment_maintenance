package com.apartment.maintenance.scheduler;

import com.apartment.maintenance.service.DueReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DueReminderScheduler {

    private final DueReminderService dueReminderService;

    // Runs daily at 9 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDueReminders() {
        dueReminderService.sendDueReminders();
    }
}