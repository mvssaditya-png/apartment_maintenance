package com.apartment.maintenance.scheduler;

import com.apartment.maintenance.service.ScheduledPaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestScheduler {

    private final ScheduledPaymentRequestService scheduledPaymentRequestService;

    // Runs every month on 1st at 6:00 AM
    @Scheduled(cron = "0 0 6 1 * *")
    public void generateMonthlyPaymentRequests() {
        scheduledPaymentRequestService.runMonthlyScheduler();
    }
}