package com.apartment.maintenance.service;

import com.apartment.maintenance.entity.MaintenancePayment;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.MaintenancePaymentRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DueReminderService {

    private final MaintenancePaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    @Transactional
    public void sendDueReminders() {

        List<MaintenancePayment> payments =
                paymentRepo.findPaymentsForDueReminder();

        for (MaintenancePayment payment : payments) {

            List<User> users =
                    userRepo.findByFlatId(payment.getFlatId());

            for (User user : users) {

                notificationService.notifyUser(
                        user.getUserId(),
                        payment.getSiteId(),
                        "Maintenance Due Reminder",
                        "Your maintenance payment of ₹"
                                + payment.getAmount()
                                + " for "
                                + payment.getPaymentMonth()
                                + "/"
                                + payment.getPaymentYear()
                                + " is pending.",
                        "DUE_REMINDER"
                );
            }

            payment.setLastReminderSentAt(LocalDateTime.now());
            paymentRepo.save(payment);
        }
    }
}