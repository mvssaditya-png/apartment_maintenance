package com.apartment.maintenance.service;

import com.apartment.maintenance.config.OtpConfig;
import com.apartment.maintenance.entity.OtpVerification;
import com.apartment.maintenance.repository.OtpVerificationRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final OtpConfig otpConfig;

    public OtpService(OtpVerificationRepository otpRepository,OtpConfig otpConfig) {
        this.otpRepository = otpRepository;
        this.otpConfig = otpConfig;
    }

    public String generateOtp(String phoneNumber) {

        LocalDateTime fifteenMinutesAgo =
                LocalDateTime.now()
                        .minusMinutes(otpConfig.getRequestWindowMinutes());

        long requestCount =
                otpRepository.countOtpRequestsSince(
                        phoneNumber,
                        fifteenMinutesAgo
                );

        if (requestCount >= otpConfig.getMaxRequests()){
            throw new RuntimeException(
                    "Too many OTP requests. Please try again after "
                            + otpConfig.getRequestWindowMinutes()
                            + " minutes."
            );
        }

        otpRepository.invalidatePreviousOtps(phoneNumber);

        String otp =
                String.valueOf(
                        100000 + secureRandom.nextInt(900000)
                );

        OtpVerification otpVerification =
                new OtpVerification();

        otpVerification.setOtpId(UUID.randomUUID());
        otpVerification.setPhoneNumber(phoneNumber);
        otpVerification.setOtp(otp);
        otpVerification.setExpiresAt(
                LocalDateTime.now().plusMinutes(otpConfig.getExpiryMinutes())
        );
        otpVerification.setVerified(false);
        otpVerification.setCreatedAt(
                LocalDateTime.now()
        );

        otpRepository.save(otpVerification);

        return otp;
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        OtpVerification latestOtp =
                otpRepository.findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                        .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (Boolean.TRUE.equals(latestOtp.getVerified())) {
            throw new RuntimeException("OTP already used");
        }

        if (LocalDateTime.now().isAfter(latestOtp.getExpiresAt())) {
            throw new RuntimeException("OTP expired");
        }

        if (!latestOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        latestOtp.setVerified(true);
        otpRepository.save(latestOtp);

        return true;
    }
}