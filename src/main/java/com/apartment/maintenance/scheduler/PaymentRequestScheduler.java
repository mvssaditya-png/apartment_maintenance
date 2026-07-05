package com.apartment.maintenance.scheduler;

import com.apartment.maintenance.service.ScheduledPaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestScheduler {

    private final ScheduledPaymentRequestService scheduledPaymentRequestService;

    @Scheduled(cron = "0 0 2 * * *")
    public void generateMonthlyPaymentRequests() {
        scheduledPaymentRequestService.runMonthlyScheduler();
    }
}