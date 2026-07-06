package com.apartment.maintenance.util;

import com.apartment.maintenance.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class SmsVariables {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

    private SmsVariables() {
    }

    public static Map<String, String> loginOtp(String otp) {
        return Map.of(
                "VAR1", safe(otp)
        );
    }

    public static Map<String, String> paymentRequestCreated(User user, PaymentRequest paymentRequest) {
        return Map.of(
                "VAR1", safe(user.getName()),
                "VAR2", amount(paymentRequest.getAmount()),
                "VAR3", safe(user.getFlatNumber()),
                "VAR4", date(paymentRequest.getDueDate())
        );
    }

    public static Map<String, String> paymentDueReminder(User user, BigDecimal amount, String flatNumber, LocalDate dueDate) {
        return Map.of(
                "VAR1", safe(flatNumber),
                "VAR2", amount(amount),
                "VAR3", date(dueDate)
        );
    }

    public static Map<String, String> paymentSubmitted(User user, MaintenancePayment payment) {
        return Map.of(
                "VAR1", safe(user.getFlatNumber()),
                "VAR2", amount(payment.getAmount())
        );
    }

    public static Map<String, String> paymentApproved(User user, MaintenancePayment payment) {
        return Map.of(
                "VAR1", safe(user.getFlatNumber()),
                "VAR2", amount(payment.getAmount())
        );
    }

    public static Map<String, String> paymentRejected(User user, MaintenancePayment payment, String reason) {
        return Map.of(
                "VAR1", safe(user.getFlatNumber()),
                "VAR2", amount(payment.getAmount()),
                "VAR3", safe(reason)
        );
    }

    public static Map<String, String> directPaymentRecorded(User user, MaintenancePayment payment) {
        return Map.of(
                "VAR1", amount(payment.getAmount()),
                "VAR2", safe(user.getFlatNumber())
        );
    }

    public static Map<String, String> noticeCreated(User user, Notice notice) {
        return Map.of(
                "VAR1", safe(notice.getTitle())
        );
    }

    public static Map<String, String> meetingCreated(User user, Meeting meeting) {
        return Map.of(
                "VAR1", safe(meeting.getTitle()),
                "VAR2", meeting.getMeetingDate() == null ? "-" : meeting.getMeetingDate().format(DATE_TIME_FORMAT)
        );
    }

    public static Map<String, String> complaintCreated(User user, Complaint complaint) {
        return Map.of(
                "VAR1", safe(user.getName()),
                "VAR2", safe(complaint.getTitle())
        );
    }

    public static Map<String, String> complaintUpdated(User user, Complaint complaint) {
        return Map.of(
                "VAR1", safe(complaint.getTitle()),
                "VAR2", safe(complaint.getStatus())
        );
    }

    public static Map<String, String> trialStarted(User adminUser, Site site) {
        return Map.of(
                "VAR1", safe(site.getSiteName()),
                "VAR2", "90"
        );
    }

    public static Map<String, String> trialExpiryReminder(User adminUser, Site site, long daysLeft) {
        return Map.of(
                "VAR1", safe(site.getSiteName()),
                "VAR2", String.valueOf(daysLeft)
        );
    }

    public static Map<String, String> trialExpired(User adminUser, Site site) {
        return Map.of(
                "VAR1", safe(site.getSiteName())
        );
    }

    public static Map<String, String> subscriptionActivated(User adminUser, Site site) {
        return Map.of(
                "VAR1", safe(site.getSiteName()),
                "VAR2", date(site.getSubscriptionEndDate())
        );
    }

    public static Map<String, String> subscriptionExpiryReminder(User adminUser, Site site, long daysLeft) {
        return Map.of(
                "VAR1", safe(site.getSiteName()),
                "VAR2", String.valueOf(daysLeft)
        );
    }

    public static Map<String, String> subscriptionExpired(User adminUser, Site site) {
        return Map.of(
                "VAR1", safe(site.getSiteName())
        );
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    private static String amount(Double value) {
        return value == null ? "0" : BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private static String amount(BigDecimal value) {
        return value == null ? "0" : value.stripTrailingZeros().toPlainString();
    }

    private static String date(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FORMAT);
    }
}