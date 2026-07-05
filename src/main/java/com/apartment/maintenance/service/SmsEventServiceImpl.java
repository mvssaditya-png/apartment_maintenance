package com.apartment.maintenance.service;

import com.apartment.maintenance.constants.SmsTemplate;
import com.apartment.maintenance.entity.*;
import com.apartment.maintenance.util.SmsVariables;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class SmsEventServiceImpl implements SmsEventService {

    private final SmsService smsService;

    public SmsEventServiceImpl(SmsService smsService) {
        this.smsService = smsService;
    }

    @Async
    @Override
    public void sendLoginOtp(String phoneNumber, String otp) {
        smsService.sendSms(phoneNumber, SmsTemplate.APP_LOGIN_OTP, SmsVariables.loginOtp(otp));
    }

    @Async
    @Override
    public void paymentRequestCreated(User user, PaymentRequest paymentRequest) {
        if (user == null || paymentRequest == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.PAYMENT_REQUEST_CREATED,
                SmsVariables.paymentRequestCreated(user, paymentRequest));
    }

    @Async
    @Override
    public void paymentDueReminder(User user, BigDecimal amount, String flatNumber, LocalDate dueDate) {
        if (user == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.PAYMENT_DUE_REMINDER,
                SmsVariables.paymentDueReminder(user, amount, flatNumber, dueDate));
    }

    @Async
    @Override
    public void paymentSubmitted(User user, MaintenancePayment payment) {
        if (user == null || payment == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.PAYMENT_SUBMITTED,
                SmsVariables.paymentSubmitted(user, payment));
    }

    @Async
    @Override
    public void paymentApproved(User user, MaintenancePayment payment) {
        if (user == null || payment == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.PAYMENT_APPROVED,
                SmsVariables.paymentApproved(user, payment));
    }

    @Async
    @Override
    public void paymentRejected(User user, MaintenancePayment payment, String reason) {
        if (user == null || payment == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.PAYMENT_REJECTED,
                SmsVariables.paymentRejected(user, payment, reason));
    }

    @Async
    @Override
    public void directPaymentRecorded(User user, MaintenancePayment payment) {
        if (user == null || payment == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.DIRECT_PAYMENT_RECORDED,
                SmsVariables.directPaymentRecorded(user, payment));
    }

    @Async
    @Override
    public void noticeCreated(User user, Notice notice) {
        if (user == null || notice == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.NOTICE_CREATED,
                SmsVariables.noticeCreated(user, notice));
    }

    @Async
    @Override
    public void meetingCreated(User user, Meeting meeting) {
        if (user == null || meeting == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.MEETING_CREATED,
                SmsVariables.meetingCreated(user, meeting));
    }

    @Async
    @Override
    public void complaintCreated(User user, Complaint complaint) {
        if (user == null || complaint == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.COMPLAINT_CREATED,
                SmsVariables.complaintCreated(user, complaint));
    }

    @Async
    @Override
    public void complaintUpdated(User user, Complaint complaint) {
        if (user == null || complaint == null) return;
        smsService.sendSms(user.getPhoneNumber(), SmsTemplate.COMPLAINT_UPDATED,
                SmsVariables.complaintUpdated(user, complaint));
    }

    @Async
    @Override
    public void trialStarted(User adminUser, Site site) {
        if (adminUser == null || site == null) return;
        smsService.sendSms(adminUser.getPhoneNumber(), SmsTemplate.TRIAL_STARTED,
                SmsVariables.trialStarted(adminUser, site));
    }

    @Async
    @Override
    public void trialExpiryReminder(User adminUser, Site site, long daysLeft) {
        if (adminUser == null || site == null) return;
        smsService.sendSms(adminUser.getPhoneNumber(), SmsTemplate.TRIAL_EXPIRY_REMINDER,
                SmsVariables.trialExpiryReminder(adminUser, site, daysLeft));
    }

    @Async
    @Override
    public void trialExpired(User adminUser, Site site) {
        if (adminUser == null || site == null) return;
        smsService.sendSms(adminUser.getPhoneNumber(), SmsTemplate.TRIAL_EXPIRED,
                SmsVariables.trialExpired(adminUser, site));
    }

    @Async
    @Override
    public void subscriptionActivated(User adminUser, Site site) {
        if (adminUser == null || site == null) return;
        smsService.sendSms(adminUser.getPhoneNumber(), SmsTemplate.SUBSCRIPTION_ACTIVATED,
                SmsVariables.subscriptionActivated(adminUser, site));
    }

    @Async
    @Override
    public void subscriptionExpiryReminder(User adminUser, Site site, long daysLeft) {
        if (adminUser == null || site == null) return;
        smsService.sendSms(adminUser.getPhoneNumber(), SmsTemplate.SUBSCRIPTION_EXPIRY_REMINDER,
                SmsVariables.subscriptionExpiryReminder(adminUser, site, daysLeft));
    }

    @Async
    @Override
    public void subscriptionExpired(User adminUser, Site site) {
        if (adminUser == null || site == null) return;
        smsService.sendSms(adminUser.getPhoneNumber(), SmsTemplate.SUBSCRIPTION_EXPIRED,
                SmsVariables.subscriptionExpired(adminUser, site));
    }
}
