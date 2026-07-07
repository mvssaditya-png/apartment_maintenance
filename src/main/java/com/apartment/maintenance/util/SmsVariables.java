package com.apartment.maintenance.util;

import com.apartment.maintenance.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

public final class SmsVariables {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a");

    private SmsVariables() {
    }

    public static Map<String, String> loginOtp(String otp) {
        return Map.of(
                "var1", safe(otp)
        );
    }

    public static Map<String, String> paymentRequestCreated(
            User user,
            PaymentRequest paymentRequest
    ) {
        return Map.of(
                "var1", safe(paymentRequest.getTitle()),
                "var2", amount(paymentRequest.getAmount()),
                "var3", safe(user.getFlatNumber()),
                "var4", date(paymentRequest.getDueDate())
        );
    }

    public static Map<String, String> paymentDueReminder(
            User user,
            BigDecimal amount,
            String flatNumber,
            LocalDate dueDate
    ) {
        return Map.of(
                "var1", "maintenance",
                "var2", amount(amount),
                "var3", "Flat " + safe(flatNumber),
                "var4", date(dueDate)
        );
    }

    public static Map<String, String> paymentSubmitted(
            User user,
            MaintenancePayment payment
    ) {
        return Map.of(
                "var1", "maintenance",
                "var2", amount(payment.getAmount())
        );
    }

    public static Map<String, String> paymentApproved(
            User user,
            MaintenancePayment payment
    ) {
        return Map.of(
                "var1", "maintenance",
                "var2", amount(payment.getAmount())
        );
    }

    public static Map<String, String> paymentRejected(
            User user,
            MaintenancePayment payment,
            String reason
    ) {
        return Map.of(
                "var1", "maintenance",
                "var2", amount(payment.getAmount()),
                "var3", safe(reason)
        );
    }

    public static Map<String, String> directPaymentRecorded(
            User user,
            MaintenancePayment payment
    ) {
        return Map.of(
                "var1", amount(payment.getAmount()),
                "var2", safe(user.getFlatNumber())
        );
    }

    public static Map<String, String> noticeCreated(
            User user,
            Notice notice
    ) {
        return Collections.emptyMap();
    }

    public static Map<String, String> meetingCreated(
            User user,
            Meeting meeting
    ) {
        return Map.of(
                "var1", meeting.getMeetingDate() == null
                        ? "-"
                        : meeting.getMeetingDate().format(DATE_FORMAT),

                "var2", meeting.getMeetingDate() == null
                        ? "-"
                        : meeting.getMeetingDate().format(TIME_FORMAT),

                "var3", safe(meeting.getTitle())
        );
    }

    public static Map<String, String> complaintCreated(
            User user,
            Complaint complaint
    ) {
        return Map.of(
                "var1", safe(user.getName()),
                "var2", complaint.getComplaintId() == null
                        ? "-"
                        : complaint.getComplaintId().toString()
        );
    }

    public static Map<String, String> complaintUpdated(
            User user,
            Complaint complaint
    ) {
        return Map.of(
                "var1", safe(complaint.getTitle()),
                "var2", safe(complaint.getStatus())
        );
    }

    public static Map<String, String> trialStarted(
            User adminUser,
            Site site
    ) {
        return Map.of(
                "var1", safe(site.getSiteName()),
                "var2", date(site.getTrialEndDate())
        );
    }

    public static Map<String, String> trialExpiryReminder(
            User adminUser,
            Site site,
            long daysLeft
    ) {
        return Map.of(
                "var1", safe(site.getSiteName()),
                "var2", date(site.getTrialEndDate())
        );
    }

    public static Map<String, String> trialExpired(
            User adminUser,
            Site site
    ) {
        return Map.of(
                "var1", safe(site.getSiteName()),
                "var2", date(site.getTrialEndDate())
        );
    }

    public static Map<String, String> subscriptionActivated(
            User adminUser,
            Site site
    ) {
        return Map.of(
                "var1", safe(site.getSiteName()),
                "var2", date(site.getSubscriptionEndDate())
        );
    }

    public static Map<String, String> subscriptionExpiryReminder(
            User adminUser,
            Site site,
            long daysLeft
    ) {
        return Map.of(
                "var1", safe(site.getSiteName()),
                "var2", String.valueOf(daysLeft)
        );
    }

    public static Map<String, String> subscriptionExpired(
            User adminUser,
            Site site
    ) {
        return Map.of(
                "var1", safe(site.getSiteName())
        );
    }

    private static String safe(String value) {
        return value == null || value.isBlank()
                ? "-"
                : value.trim();
    }

    private static String amount(Double value) {
        return value == null
                ? "0"
                : BigDecimal.valueOf(value)
                  .stripTrailingZeros()
                  .toPlainString();
    }

    private static String amount(BigDecimal value) {
        return value == null
                ? "0"
                : value.stripTrailingZeros().toPlainString();
    }

    private static String date(LocalDate date) {
        return date == null
                ? "-"
                : date.format(DATE_FORMAT);
    }
}