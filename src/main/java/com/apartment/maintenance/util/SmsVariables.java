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
        return Map.of("otp", safe(otp));
    }

    public static Map<String, String> paymentRequestCreated(User user, PaymentRequest paymentRequest) {
        return Map.of(
                "name", safe(user.getName()),
                "amount", amount(paymentRequest.getAmount()),
                "flat", safe(user.getFlatNumber()),
                "due_date", date(paymentRequest.getDueDate())
        );
    }

    public static Map<String, String> paymentDueReminder(User user, BigDecimal amount, String flatNumber, LocalDate dueDate) {
        return Map.of(
                "name", safe(user.getName()),
                "amount", amount(amount),
                "flat", safe(flatNumber),
                "due_date", date(dueDate)
        );
    }

    public static Map<String, String> paymentSubmitted(User user, MaintenancePayment payment) {
        return paymentBasic(user, payment);
    }

    public static Map<String, String> paymentApproved(User user, MaintenancePayment payment) {
        return paymentBasic(user, payment);
    }

    public static Map<String, String> paymentRejected(User user, MaintenancePayment payment, String reason) {
        return Map.of(
                "name", safe(user.getName()),
                "amount", amount(payment.getAmount()),
                "flat", safe(user.getFlatNumber()),
                "reason", safe(reason)
        );
    }

    public static Map<String, String> directPaymentRecorded(User user, MaintenancePayment payment) {
        return paymentBasic(user, payment);
    }

    public static Map<String, String> noticeCreated(User user, Notice notice) {
        return Map.of(
                "name", safe(user.getName()),
                "notice_title", safe(notice.getTitle())
        );
    }

    public static Map<String, String> meetingCreated(User user, Meeting meeting) {
        return Map.of(
                "name", safe(user.getName()),
                "meeting_title", safe(meeting.getTitle()),
                "meeting_date", meeting.getMeetingDate() == null ? "-" : meeting.getMeetingDate().format(DATE_TIME_FORMAT)
        );
    }

    public static Map<String, String> complaintCreated(User user, Complaint complaint) {
        return Map.of(
                "name", safe(user.getName()),
                "complaint_title", safe(complaint.getTitle())
        );
    }

    public static Map<String, String> complaintUpdated(User user, Complaint complaint) {
        return Map.of(
                "name", safe(user.getName()),
                "complaint_title", safe(complaint.getTitle()),
                "status", safe(complaint.getStatus())
        );
    }

    public static Map<String, String> trialStarted(User adminUser, Site site) {
        return Map.of(
                "name", safe(adminUser.getName()),
                "society", safe(site.getSiteName()),
                "days", "90"
        );
    }

    public static Map<String, String> trialExpiryReminder(User adminUser, Site site, long daysLeft) {
        return Map.of(
                "name", safe(adminUser.getName()),
                "society", safe(site.getSiteName()),
                "days", String.valueOf(daysLeft)
        );
    }

    public static Map<String, String> trialExpired(User adminUser, Site site) {
        return nameSociety(adminUser, site);
    }

    public static Map<String, String> subscriptionActivated(User adminUser, Site site) {
        return Map.of(
                "name", safe(adminUser.getName()),
                "society", safe(site.getSiteName()),
                "expiry_date", date(site.getSubscriptionEndDate())
        );
    }

    public static Map<String, String> subscriptionExpiryReminder(User adminUser, Site site, long daysLeft) {
        return Map.of(
                "name", safe(adminUser.getName()),
                "society", safe(site.getSiteName()),
                "days", String.valueOf(daysLeft)
        );
    }

    public static Map<String, String> subscriptionExpired(User adminUser, Site site) {
        return nameSociety(adminUser, site);
    }

    private static Map<String, String> paymentBasic(User user, MaintenancePayment payment) {
        return Map.of(
                "name", safe(user.getName()),
                "amount", amount(payment.getAmount()),
                "flat", safe(user.getFlatNumber())
        );
    }

    private static Map<String, String> nameSociety(User user, Site site) {
        return Map.of(
                "name", safe(user.getName()),
                "society", safe(site.getSiteName())
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
