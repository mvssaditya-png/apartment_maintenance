package com.apartment.maintenance.service;

import com.apartment.maintenance.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SmsEventService {

    void sendLoginOtp(String phoneNumber, String otp);

    void paymentRequestCreated(User user, PaymentRequest paymentRequest);

    void paymentDueReminder(User user, BigDecimal amount, String flatNumber, LocalDate dueDate);

    void paymentSubmitted(User user, MaintenancePayment payment);

    void paymentApproved(User user, MaintenancePayment payment);

    void paymentRejected(User user, MaintenancePayment payment, String reason);

    void directPaymentRecorded(User user, MaintenancePayment payment);

    void noticeCreated(User user, Notice notice);

    void meetingCreated(User user, Meeting meeting);

    void complaintCreated(User user, Complaint complaint);

    void complaintUpdated(User user, Complaint complaint);

    void trialStarted(User adminUser, Site site);

    void trialExpiryReminder(User adminUser, Site site, long daysLeft);

    void trialExpired(User adminUser, Site site);

    void subscriptionActivated(User adminUser, Site site);

    void subscriptionExpiryReminder(User adminUser, Site site, long daysLeft);

    void subscriptionExpired(User adminUser, Site site);
}